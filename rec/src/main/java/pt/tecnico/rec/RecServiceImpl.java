package pt.tecnico.bicloin.rec;

import io.grpc.stub.StreamObserver;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.ALREADY_EXISTS;

public class RecServiceImpl extends RecGrpc.RecImplBase {

  @Override
  public void ctrl_ping(CtrlPingRequest request, StreamObserver<CtrlPingResponse> observerResponse) {

  }

  @Override
  public void read(ReadRequest request, StreamObserver<ReadResponse> observerResponse) {
    
  }

  @Override
  public void write(WriteRequest request, StreamObserver<WriteResponse> observerResponse) {

  }



}
