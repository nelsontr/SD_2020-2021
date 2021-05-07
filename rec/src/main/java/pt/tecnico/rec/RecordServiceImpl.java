package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.*;

import java.util.HashMap;
import java.util.Map;

import static io.grpc.Status.INVALID_ARGUMENT;

public class RecordServiceImpl extends RecordGrpc.RecordImplBase {

    private final Map<String, Record> _records = new HashMap<>();

    private final String OK_RESPONSE = "OK";
    private final String READ_ERROR_RESPONSE = "Read operation requires a Rec name";
    private final String REQUEST_EMPTY = "Request cannot be empty!";
    private final String INTEGER_BELOW_ZERO = "Request came with a inputValue lower than zero!";

    @Override
    public void ctrlPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        String input = request.getInput();

        if (input == null || input.isBlank()) {
            synchronized (responseObserver) {
                responseObserver.onError(INVALID_ARGUMENT.withDescription(REQUEST_EMPTY).asRuntimeException());
            }
            return;
        }

        String output = input;
        PingResponse response = PingResponse.newBuilder().setOutput(output).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {

        String input = request.getName();

        if (input == null || input.isBlank()) {
            synchronized (responseObserver) {
                responseObserver.onError(INVALID_ARGUMENT.withDescription(READ_ERROR_RESPONSE).asRuntimeException());
            }
            return;
        }

        synchronized (this) {
            Record readRecord = _records.get(input);
            if (readRecord == null) {
                readRecord = new Record(input, -1, 0, 0);
                _records.put(input, readRecord);
            }

            ReadResponse response = ReadResponse.newBuilder().setValue(readRecord.getValue())
                    .setSequence(readRecord.getSequence()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {

        final String input = request.getName();
        final int inputValue = request.getIntValue();

        if (input == null || input.isBlank()) {
            synchronized (responseObserver) {
                responseObserver.onError(INVALID_ARGUMENT.withDescription(REQUEST_EMPTY).asRuntimeException());
            }
            return;
        } else if (inputValue < 0) {
            synchronized (responseObserver) {
                responseObserver.onError(INVALID_ARGUMENT.withDescription(INTEGER_BELOW_ZERO).asRuntimeException());
            }
            return;
        }

        final int inputSequente = request.getSequence();
        final int inputCid = request.getCid();

        synchronized (this) {

            Record readRecord = _records.get(input);

            if (readRecord == null) {
                readRecord = new Record(input, inputValue, inputSequente, inputCid);
                _records.put(input, readRecord);
            } else if (inputSequente > readRecord.getSequence() || (inputSequente == readRecord.getSequence() && inputCid > readRecord.getCid())) {
                readRecord.setValue(inputValue);
                readRecord.setSequence(inputSequente);
                readRecord.setCid(inputCid);
                _records.put(readRecord.getName(), readRecord);
            }

            final WriteResponse response = WriteResponse.newBuilder().setResponse(OK_RESPONSE).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void clearRecords(ClearRequest request, StreamObserver<ClearResponse> responseObserver) {
        synchronized (this) {
            _records.clear();

            ClearResponse response = ClearResponse.newBuilder().build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

    }

    @Override
    public void sysStat(SysStatRequest request, StreamObserver<SysStatResponse> responseObserver) {

        SysStatResponse response = SysStatResponse.newBuilder().setStatus("UP").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
