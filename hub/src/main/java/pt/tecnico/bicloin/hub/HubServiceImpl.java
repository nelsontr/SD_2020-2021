package pt.tecnico.hub;

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

  //OVERRIDES MISSING
  //@Override RESOLVE
  public void ctrl_ping(CtrlPingRequest request, StreamObserver<CtrlPingResponse> responseObserver){

    String input = request.getInput();

    if (input.isBlank()) {
        responseObserver.onError(INVALID_ARGUMENT.withDescription("You must type an input").asRuntimeException());

        return;
    }
    CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput("Hi: " + input ).build();


  }


  public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver){

  }


  public void top_up(TopUpRequest request, StreamObserver<TopUpResponse> responseObserver){

  }


  public void info_station(InfoStationRequest request, StreamObserver<InfoStationResponse> responseObserver){

  }


  public void locate_station(LocateStationRequest request, StreamObserver<LocateStationResponse> responseObserver){

  }


  public void bike_up(BikeRequest request, StreamObserver<BikeResponse> responseObserver){

  }


  public void bike_down(BikeRequest request, StreamObserver<BikeResponse> responseObserver){

  }


  public void sys_status(SysStatusRequest request, StreamObserver<SysStatusResponse> responseObserver){

  }

}
