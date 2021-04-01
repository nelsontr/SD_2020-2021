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


  
  public void ctrl_ping(CtrlPingRequest request, StreamObserver<CtrlPingResponse> observerResponse) {

  }


  public void read(ReadRequest request, StreamObserver<ReadResponse> observerResponse) {

  }


  public void write(WriteRequest request, StreamObserver<WriteResponse> observerResponse) {

  }



}
