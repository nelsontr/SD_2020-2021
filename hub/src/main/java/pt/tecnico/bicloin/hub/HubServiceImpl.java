package pt.tecnico.bicloin.hub;

import io.grpc.stub.StreamObserver;

import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.ALREADY_EXISTS;

import pt.tecnico.bicloin.hub.grpc.*;

public class HubServiceImpl extends HubGrpc.HubImplBase {

  Hub data = new Hub();


  @Override
  public void ping(CtrlPingRequest request, StreamObserver<CtrlPingResponse> responseObserver){
    String input = request.getInput();

    if (input == null || input.isBlank()) {
          responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
    }

    String output = "Hello " + input + "!";

    CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput(output).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver){

  }

  @Override
  public void topUp(TopUpRequest request, StreamObserver<TopUpResponse> responseObserver){

  }

  @Override
  public void infoStation(InfoStationRequest request, StreamObserver<InfoStationResponse> responseObserver){

  }

  @Override
  public void locateStation(LocateStationRequest request, StreamObserver<LocateStationResponse> responseObserver){

  }

  @Override
  public void bikeUp(BikeRequest request, StreamObserver<BikeResponse> responseObserver){

  }

  @Override
  public void bikeDown(BikeRequest request, StreamObserver<BikeResponse> responseObserver){

  }


  public void sys_status(SysStatusRequest request, StreamObserver<SysStatusResponse> responseObserver){

  }

}
