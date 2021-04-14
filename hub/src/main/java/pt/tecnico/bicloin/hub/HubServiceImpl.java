package pt.tecnico.bicloin.hub;

import io.grpc.stub.StreamObserver;


import java.util.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.FAILED_PRECONDITION;
import static io.grpc.Status.CANCELLED;

import pt.tecnico.bicloin.hub.grpc.*;
import pt.tecnico.bicloin.hub.*;
import pt.tecnico.rec.grpc.*;
import pt.tecnico.rec.*;

public class HubServiceImpl extends HubGrpc.HubImplBase {

  final static String OK = "OK";
  final static String ERROR = "ERRO fora de alcance";

  Hub data = new Hub();
  //Mal , estou forcing it mas para já....
  RecFrontend _rec;


  HubServiceImpl() {
    _rec = new RecFrontend("localhost", "8091");
    Runtime.getRuntime().addShutdownHook(new CloseServer());
  }


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
    String userName = request.getUserName();
    int balance = -1;

    if (userName == null || userName.isBlank()) {
          responseObserver.onError(INVALID_ARGUMENT.withDescription("UserName cannot be empty!").asRuntimeException());
    }

    ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + "/user/Balance").build();
    balance = _rec.read(balanceRequest).getValue();

    BalanceResponse response = BalanceResponse.newBuilder().setBalance(balance).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void topUp(TopUpRequest request, StreamObserver<TopUpResponse> responseObserver){
    String userName = request.getUserName();
    int stake = request.getStake();
    int phoneNumber = request.getPhoneNumber();
    int balance = -1;

    if (stake < 1 || stake > 20){
      responseObserver.onError(INVALID_ARGUMENT.withDescription("Stake has to be in range [1, 20]!").asRuntimeException());
    }

    if (userName == null || userName.isBlank()) {
          responseObserver.onError(INVALID_ARGUMENT.withDescription("UserName cannot be empty!").asRuntimeException());
    }

    if(data.getUser(userName).getPhoneNumber() != phoneNumber){
      responseObserver.onError(INVALID_ARGUMENT.withDescription("UserName has a different PhoneNumber linked than the one provided!").asRuntimeException());
    }

    ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + "/user/Balance").build();
    balance = _rec.read(balanceRequest).getValue();

    balance = balance + stake*10;
    WriteRequest newBalanceRequest = WriteRequest.newBuilder().setName(userName + "/user/Balance").setIntValue(balance).build();

    TopUpResponse response = TopUpResponse.newBuilder().setBalance(balance).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void infoStation(InfoStationRequest request, StreamObserver<InfoStationResponse> responseObserver){
    String stationId = request.getStationId();

    if (stationId.length() != 4) {
          responseObserver.onError(INVALID_ARGUMENT.withDescription("Station Id must be 4 letters long").asRuntimeException());
    }

    Station station = data.getStation(stationId);
    String stationName = station.getName();
    double latitude = station.getLat();
    double longitude = station.getLong();
    int dockCapacity = station.getDockCapacity();
    int prize = station.getPrize();
    //VERIFICAR COMO FOI IMPLEMENTADO O READ + probably should have try catchs
    //ADD MACROS
    ReadRequest bikesRequest = ReadRequest.newBuilder().setName(stationId + "/station/AvailableBikes").build();
    ReadRequest pickupsRequest = ReadRequest.newBuilder().setName(stationId + "/station/Pickups").build();
    ReadRequest returnsRequest = ReadRequest.newBuilder().setName(stationId + "/station/Returns").build();
    int availableBikes = _rec.read(bikesRequest).getValue();
    int pickups = _rec.read(pickupsRequest).getValue();
    int returns = _rec.read(returnsRequest).getValue();

    InfoStationResponse response = InfoStationResponse.newBuilder().setName(stationName).setLat(latitude).setLong(longitude).setDockCapacity(dockCapacity).setPrize(prize).setAvailableBikes(availableBikes).setPickups(pickups).setReturns(returns).build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void locateStation(LocateStationRequest request, StreamObserver<LocateStationResponse> responseObserver){
    double latitude = request.getLat();
    double longitude = request.getLong();
    int number = request.getNumStations();

    HashMap<String, Integer> results = new HashMap<>();
    Map<String, Station> stations = data.getStations();

    Iterator iterator = stations.entrySet().iterator();

    while(iterator.hasNext()) {
      Map.Entry nextStation = (Map.Entry)iterator.next();
      Station station = (Station)nextStation.getValue();
      double lt = station.getLat();
      double lg = station.getLong();
      //distante between latitudes and longitudes
      int distance = this.getDistance(latitude, longitude, lt, lg);

      results.put(station.getId(), distance);
    }

    LinkedHashMap<String, Integer> orderedResults = this.orderedResults(results, number);
    Set<String> keys = orderedResults.keySet();
    LocateStationResponse.Builder b = LocateStationResponse.newBuilder();

    for(String key : keys) {
      int index = 0;
      b.addScan(Scan.newBuilder().setStationId(index, key).setDistance(index, orderedResults.get(key)).build());
      index++;
    }

    LocateStationResponse response = b.build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();

  }

  @Override
  public void bikeUp(BikeRequest request, StreamObserver<BikeResponse> responseObserver){
    BikeResponse response;
    String userName = request.getUserName();
    double latitude = request.getLat();
    double longitude = request.getLong();
    String stationId = request.getStationId();

    Station station = data.getStation(stationId);
    double stationLat = station.getLat();
    double stationLong = station.getLong();
    int distance = getDistance(latitude, longitude, stationLat, stationLong);

    ReadRequest availableBikesRequest = ReadRequest.newBuilder().setName(stationId + "/station/AvailableBikes").build();
    int availableBikes = _rec.read(availableBikesRequest).getValue();

    if (distance >= 200 || availableBikes == 0){
      response = BikeResponse.newBuilder().setStatus(ERROR).build();
    }
    else{
      WriteRequest newAvailableBikesRequest = WriteRequest.newBuilder().setName(stationId + "/station/AvailableBikes").setIntValue(availableBikes - 1).build();
      ReadRequest pickupsRequest = ReadRequest.newBuilder().setName(stationId + "/station/Pickups").build();
      int pickups = _rec.read(pickupsRequest).getValue() + 1;

      WriteRequest newPickupsRequest = WriteRequest.newBuilder().setName(stationId + "station/Pickups").setIntValue(pickups).build();

      response = BikeResponse.newBuilder().setStatus(OK).build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void bikeDown(BikeRequest request, StreamObserver<BikeResponse> responseObserver){
    BikeResponse response;
    String userName = request.getUserName();
    double latitude = request.getLat();
    double longitude = request.getLong();
    String stationId = request.getStationId();

    Station station = data.getStation(stationId);
    double stationLat = station.getLat();
    double stationLong = station.getLong();
    int distance = getDistance(latitude, longitude, stationLat, stationLong);

    if (distance >= 200){
      response = BikeResponse.newBuilder().setStatus(ERROR).build();
    }
    else{
      int prize = station.getPrize();

      ReadRequest returnsRequest = ReadRequest.newBuilder().setName(stationId + "/station/Returns").build();
      int returns = _rec.read(returnsRequest).getValue() + 1;

      ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + "/user/Balance").build();
      int balance = _rec.read(balanceRequest).getValue() + prize;

      ReadRequest availableBikesRequest = ReadRequest.newBuilder().setName(stationId + "/station/AvailableBikes").build();
      int availableBikes = _rec.read(availableBikesRequest).getValue() + 1;

      WriteRequest newReturnsRequest = WriteRequest.newBuilder().setName(stationId + "/station/Returns").build();
      WriteRequest newBalanceRequest = WriteRequest.newBuilder().setName(userName + "/user/Balance").build();
      WriteRequest newAvailableBikesRequest = WriteRequest.newBuilder().setName(stationId + "station/AvailableBikes").build();

      response = BikeResponse.newBuilder().setStatus(OK).build();
    }

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }


  public void sys_status(SysStatusRequest request, StreamObserver<SysStatusResponse> responseObserver){

  }


  private LinkedHashMap<String, Integer> orderedResults(HashMap<String, Integer> toOrder, int number){

    List<Map.Entry<String, Integer> > list =
               new LinkedList<Map.Entry<String, Integer>>(toOrder.entrySet());

    // Sort the list
    Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
        public int compare(Map.Entry<String, Integer> o1,
                           Map.Entry<String, Integer> o2)
        {
            return (o1.getValue()).compareTo(o2.getValue());
        }
    });

    // put data from sorted list to hashmap
    LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, Integer> aa : list) {
        if(temp.size() == number){
          break;
        }
        temp.put(aa.getKey(), aa.getValue());
    }
    return temp;
    }


    public int getDistance(double latitude, double longitude, double stationLat, double stationLong){
      double dLat = Math.toRadians(stationLat - latitude);
      double dLong = Math.toRadians(stationLong - longitude);
      //formula
      double a = Math.pow(Math.sin(dLat / 2), 2) +
                  Math.pow(Math.sin(dLong / 2), 2) *
                  Math.cos(latitude) *
                  Math.cos(stationLat);
      double rad = 6371;
      double c = 2 * Math.asin(Math.sqrt(a));

      return ((int)((rad * c) * 1000)); //result in meters
    }

    private final class CloseServer extends Thread {
      @Override
      public void run() {
        _rec.closeChannel();
      }

    }
}
