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


  @Override
  public void ctrlPing(CtrlPingRequest request, StreamObserver<CtrlPingResponse> responseObserver) {
        String input = request.getInput();

        if (input == null || input.isBlank()) {
		          responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
	      }

    		String output = "Hello " + input + "!";

    		CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput(output).build();

    		responseObserver.onNext(response);
    		responseObserver.onCompleted();
  }


  public void read(ReadRequest request, StreamObserver<ReadResponse> observerResponse) {

  }


  public void write(WriteRequest request, StreamObserver<WriteResponse> observerResponse) {

  }



}
