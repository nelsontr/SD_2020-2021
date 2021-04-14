package pt.tecnico.rec;

import io.grpc.stub.StreamObserver;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.ALREADY_EXISTS;

import pt.tecnico.rec.grpc.*;

public class RecordServiceImpl extends RecordGrpc.RecordImplBase {

    private Map<String, Integer> _records = new HashMap<>();

    private final String OK_RESPONSE = "OK";
    private final String ERROR_RESPONSE = "ERROR";
    private final String NO_INPUT_FOUND = "No {} was found!";
    private final String REQUEST_EMPTY = "Request cannot be empty!";
    private final String INTEGER_BELOW_ZERO = "Request came with a inputValue lower than zero!";

    @Override
    public void ctrlPing(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        String input = request.getInput();

        if (input == null || input.isBlank()) {
        responseObserver.onError(INVALID_ARGUMENT.withDescription(REQUEST_EMPTY).asRuntimeException());
        }

        String output = "Hello " + input + "!";
        PingResponse response = PingResponse.newBuilder().setOutput(output).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadResponse> observerResponse) {

        String input = request.getName();

        if (input == null || input.isBlank()) {
            observerResponse.onError(INVALID_ARGUMENT.withDescription(REQUEST_EMPTY).asRuntimeException());
        } else if (!_records.containsKey(input)) {
            observerResponse.onError(NOT_FOUND.withDescription(String.format(NO_INPUT_FOUND, input)).asRuntimeException());
        }

        int output = _records.get(input);
        ReadResponse response =  ReadResponse.newBuilder().setValue(output).build();

        observerResponse.onNext(response);
        observerResponse.onCompleted();
    }

    @Override
    public void write(WriteRequest request, StreamObserver<WriteResponse> observerResponse) {

        String output;
        String input = request.getName();
        Integer inputValue = request.getIntValue();

        if (input == null || input.isBlank()) {
            observerResponse.onError(INVALID_ARGUMENT.withDescription(REQUEST_EMPTY).asRuntimeException());
        } else if ( inputValue<0 ) {
            observerResponse.onError(INVALID_ARGUMENT.withDescription(INTEGER_BELOW_ZERO).asRuntimeException());
        }

        _records.remove(input); //if it doesn't exist, returns null
        _records.put(input, inputValue);

        //WHERE ERROR IS?

        output = OK_RESPONSE;
        WriteResponse response = WriteResponse.newBuilder().setResponse(output).build();

        observerResponse.onNext(response);
        observerResponse.onCompleted();
    }

    @Override
    public void clearRecords(ClearRequest request, StreamObserver<ClearResponse> observerResponse) {
        Integer inputValue = request.getIntValue();

        if ( inputValue == null ) {
            observerResponse.onError(INVALID_ARGUMENT.withDescription(REQUEST_EMPTY).asRuntimeException());
        } else if ( inputValue<0 ) {
            observerResponse.onError(INVALID_ARGUMENT.withDescription(INTEGER_BELOW_ZERO).asRuntimeException());
        }

        _records.clear();

        ClearResponse response = ClearResponse.newBuilder().setResponse(1).build();

        observerResponse.onNext(response);
        observerResponse.onCompleted();
    }
}
