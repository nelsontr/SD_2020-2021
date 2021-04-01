package pt.tecnico.bicloin.hub;

import io.grpc.stub.StreamObserver;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.ALREADY_EXISTS;

public class HubServiceImpl extends HubGrpc.HubImplBase {

  @Override
  public void ctrl_ping(CtrlPingRequest request, StreamObserver<CtrlPingResponse> responseObserver){

    String input = request.getInput();

    if (input.isBlank()) {
        responseObserver.onError(INVALID_ARGUMENT.withDescription("You must type an input").asRuntimeException());

        return;
    }
    CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput("Hi: " + input ).build();

    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void top_up(TopUpRequest request, StreamObserver<TopUpResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void info_station(InfoStationRequest request, StreamObserver<InfoStationResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void locate_station(LocateStationRequest request, StreamObserver<LocateStationResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void bike_up(BikeRequest request, StreamObserver<BikeResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void bike_down(BikeRequest request, StreamObserver<BikeResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

  @Override
  public void sys_status(SysStatusRequest request, StreamObserver<SysStatusResponse> responseObserver){
    // Send a single response through the stream.
    responseObserver.onNext(response);
    // Notify the client that the operation has been completed.
    responseObserver.onCompleted();
  }

}
