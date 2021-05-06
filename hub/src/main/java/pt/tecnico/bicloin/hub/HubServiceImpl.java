package pt.tecnico.bicloin.hub;

import io.grpc.stub.StreamObserver;

import java.util.*;

import static io.grpc.Status.UNKNOWN;
import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.INVALID_ARGUMENT;

import pt.tecnico.rec.*;
import pt.tecnico.rec.grpc.*;
import pt.tecnico.bicloin.hub.grpc.*;

import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;


public class HubServiceImpl extends HubGrpc.HubImplBase {

    final static String OK = "OK";
    final static String ERROR = "ERRO Out of Reach";
    final static String ERROR_NO_MONEY = "ERRO No Money Available";
    final static String USER_BALANCE = "/user/Balance";
    final static String STATION_BIKES_AVAILABLE = "/station/AvailableBikes";
    final static String STATION_PICKUPS = "/station/Pickups";
    final static String STATION_RETURNS = "/station/Returns";

    private Hub data = new Hub();
    private QuorumFrontend _recQuorum;
    private RecFrontend _rec;
    private int _cid;


    HubServiceImpl(String zooHost, String zooPort, String cid) {
          _recQuorum = new QuorumFrontend(zooHost, zooPort);
          _rec = new RecFrontend(zooHost, zooPort);
          _cid = Integer.parseInt(cid);
          Runtime.getRuntime().addShutdownHook(new CloseServer());
    }


    @Override
    public void ping(CtrlPingRequest request, StreamObserver<CtrlPingResponse> responseObserver) {
        String input = request.getInput();

        if (input == null || input.isBlank()) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Input cannot be empty!").asRuntimeException());
            return;
        }

        PingRequest newRequest = PingRequest.newBuilder().setInput(input).build();

        String output = "Hub says hello  " + input + "!" + "\n";
        output += "Rec says hello  " + _rec.ping(newRequest).getOutput() + "!" + "\n";


        CtrlPingResponse response = CtrlPingResponse.newBuilder().setOutput(output).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        String userName = request.getUserName();

        if (userName == null || userName.isBlank()) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("UserName cannot be empty!").asRuntimeException());
            return;
        }

        ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + USER_BALANCE).build();
        synchronized(this) {
            int balance = _recQuorum.read(balanceRequest).getValue();

            if (balance == -1 && !data.existingUser(userName)) {
                responseObserver.onError(NOT_FOUND.withDescription("User does not exist in records").asRuntimeException());
                return;
            } else if (balance == -1 && data.existingUser(userName)) {
                balance = 0;
                WriteRequest balanceReq = WriteRequest.newBuilder().setName(userName + USER_BALANCE).setIntValue(0).build();
                _recQuorum.write(balanceReq);
            }

            BalanceResponse response = BalanceResponse.newBuilder().setBalance(balance).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void topUp(TopUpRequest request, StreamObserver<TopUpResponse> responseObserver) {
        String userName = request.getUserName();
        int stake = request.getStake();
        String phoneNumber = request.getPhoneNumber();

        if (stake < 1 || stake > 20) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Stake has to be in range [1, 20]!").asRuntimeException());
            return;
        } else if (userName == null || userName.isBlank()) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("UserName cannot be empty!").asRuntimeException());
            return;
        } else if (!data.existingUser(userName)) {
            responseObserver.onError(NOT_FOUND.withDescription("UserName not registered!").asRuntimeException());
            return;
        } else if (!data.getUser(userName).getPhoneNumber().equals(phoneNumber)) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("UserName has a different PhoneNumber linked than the one provided!").asRuntimeException());
            return;
        }

        synchronized (this) {
            ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + USER_BALANCE).build();
            ReadResponse balanceResponse = _recQuorum.read(balanceRequest);
            int balance = balanceResponse.getValue();
            int balanceSequence = balanceResponse.getSequence() + 1;

            balance = (balance == -1) ? 0 : balance;
            balance += stake * 10;

            WriteRequest newBalanceRequest = WriteRequest.newBuilder().setName(userName + USER_BALANCE).setIntValue(balance)
              .setSequence(balanceSequence).setCid(this._cid).build();
            if (!_recQuorum.write(newBalanceRequest).getResponse().equals("OK")) {
                responseObserver.onError(UNKNOWN.withDescription("Couldn't write").asRuntimeException());
                return;
            }

            TopUpResponse response = TopUpResponse.newBuilder().setBalance(balance).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void infoStation(InfoStationRequest request, StreamObserver<InfoStationResponse> responseObserver) {
        String stationId = request.getStationId();

        if (stationId.length() != 4) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Station Id must be 4 letters long").asRuntimeException());
            return;
        }

        Station station = null;
        String stationName = null;
        double latitude = 0;
        double longitude = 0;
        int dockCapacity = 0;
        int prize = 0;

        try {
            station = data.getStation(stationId);
            stationName = station.getName();
            latitude = station.getLat();
            longitude = station.getLong();
            dockCapacity = station.getDockCapacity();
            prize = station.getPrize();
        } catch (RuntimeException e) {
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
            return;
        }

        synchronized (this) {
            //VERIFICAR COMO FOI IMPLEMENTADO O READ + probably should have try catchs
            ReadRequest pickupsRequest = ReadRequest.newBuilder().setName(stationId + STATION_PICKUPS).build();
            ReadRequest returnsRequest = ReadRequest.newBuilder().setName(stationId + STATION_RETURNS).build();
            ReadRequest bikesRequest = ReadRequest.newBuilder().setName(stationId + STATION_BIKES_AVAILABLE).build();
            int pickups = _recQuorum.read(pickupsRequest).getValue();
            int returns = _recQuorum.read(returnsRequest).getValue();
            int availableBikes = _recQuorum.read(bikesRequest).getValue();

            returns = (returns == -1) ? 0 : returns;
            pickups = (pickups == -1) ? 0 : pickups;
            availableBikes = (availableBikes == -1) ? 0 : availableBikes;

            InfoStationResponse response = InfoStationResponse.newBuilder().setName(stationName).setLat(latitude)
                    .setLong(longitude).setDockCapacity(dockCapacity).setPrize(prize)
                    .setAvailableBikes(availableBikes).setPickups(pickups).setReturns(returns).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void locateStation(LocateStationRequest request, StreamObserver<LocateStationResponse> responseObserver) {
        double latitude = request.getLat();
        double longitude = request.getLong();
        int number = request.getNumStations();

        HashMap<String, Integer> results = new HashMap<>();
        Map<String, Station> stations = data.getStations();

        Iterator iterator = stations.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry nextStation = (Map.Entry) iterator.next();
            Station station = (Station) nextStation.getValue();
            double lt = station.getLat();
            double lg = station.getLong();
            //distante between latitudes and longitudes
            int distance = this.getDistance(latitude, longitude, lt, lg);

            results.put(station.getId(), distance);
        }

        LinkedHashMap<String, Integer> orderedResults = this.orderedResults(results, number);
        Set<String> keys = orderedResults.keySet();
        LocateStationResponse.Builder b = LocateStationResponse.newBuilder();

        synchronized (this) {
             for (String key : keys) {
            double lt = data.getStation(key).getLat();
            double lg = data.getStation(key).getLong();
            int dockCapacity = data.getStation(key).getDockCapacity();
            int prize = data.getStation(key).getPrize();
            ReadRequest availableBikes = ReadRequest.newBuilder().setName(key + STATION_BIKES_AVAILABLE).build();
            int bikesAvailable = _recQuorum.read(availableBikes).getValue();
            bikesAvailable = (bikesAvailable == -1) ? 0 : bikesAvailable;

            int distance = orderedResults.get(key);
            b.addScan(Scan.newBuilder().setStationId(key).setLat(lt).setLong(lg).setDockCapacity(dockCapacity).setPrize(prize).setAvailableBikes(bikesAvailable).setDistance(distance).build());
            }

            LocateStationResponse response = b.build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void bikeUp(BikeRequest request, StreamObserver<BikeResponse> responseObserver) {
        BikeResponse response;

        String userName = request.getUserName();
        double latitude = request.getLat();
        double longitude = request.getLong();
        String stationId = request.getStationId();
        Station station = data.getStation(stationId);
        double stationLat = station.getLat();
        double stationLong = station.getLong();
        int distance = getDistance(latitude, longitude, stationLat, stationLong);

        ReadRequest availableBikesRequest = ReadRequest.newBuilder().setName(stationId + STATION_BIKES_AVAILABLE).build();

        synchronized (this){
            ReadResponse availableBikesResponse = _recQuorum.read(availableBikesRequest);
            int availableBikes = availableBikesResponse.getValue();
            int availableBikesSequence = availableBikesResponse.getSequence() + 1;

            if (distance >= 200 || availableBikes == 0) {
                response = BikeResponse.newBuilder().setStatus(ERROR).build();
            } else {
                ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + USER_BALANCE).build();
                ReadResponse balanceResponse = _recQuorum.read(balanceRequest);
                int balance = balanceResponse.getValue() - 10;
                int balanceSequence = balanceResponse.getSequence() + 1;
                if (balance >= 0) {
                    WriteRequest newAvailableBikesRequest = WriteRequest.newBuilder().setName(stationId + STATION_BIKES_AVAILABLE)
                      .setIntValue(availableBikes - 1).setSequence(availableBikesSequence).setCid(this._cid).build();
                    _recQuorum.write(newAvailableBikesRequest);
                    ReadRequest pickupsRequest = ReadRequest.newBuilder().setName(stationId + STATION_PICKUPS).build();
                    ReadResponse pickupsResponse = _recQuorum.read(pickupsRequest);
                    int pickups = pickupsResponse.getValue();
                    int pickupsSequence = pickupsResponse.getSequence() + 1;
                    pickups = (pickups == -1) ? 1 : ++pickups;

                    WriteRequest newPickupsRequest = WriteRequest.newBuilder().setName(stationId + STATION_PICKUPS)
                      .setIntValue(pickups).setSequence(pickupsSequence).setCid(this._cid).build();
                    _recQuorum.write(newPickupsRequest);

                    WriteRequest newBalanceRequest = WriteRequest.newBuilder().setName(userName + USER_BALANCE)
                      .setIntValue(balance).setSequence(balanceSequence).setCid(this._cid).build();
                    _recQuorum.write(newBalanceRequest);
                    response = BikeResponse.newBuilder().setStatus(OK).build();
                } else response = BikeResponse.newBuilder().setStatus(ERROR_NO_MONEY).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void bikeDown(BikeRequest request, StreamObserver<BikeResponse> responseObserver) {
        BikeResponse response;
        String userName = request.getUserName();
        double latitude = request.getLat();
        double longitude = request.getLong();
        String stationId = request.getStationId();

        Station station = data.getStation(stationId);
        double stationLat = station.getLat();
        double stationLong = station.getLong();
        int distance = getDistance(latitude, longitude, stationLat, stationLong);

        synchronized (this) {
            if (distance >= 200) {
            response = BikeResponse.newBuilder().setStatus(ERROR).build();
            } else {
                int prize = station.getPrize();

                ReadRequest returnsRequest = ReadRequest.newBuilder().setName(stationId + STATION_RETURNS).build();
                ReadResponse returnsResponse = _recQuorum.read(returnsRequest);
                int returns = returnsResponse.getValue();
                int returnsSequence = returnsResponse.getSequence() + 1;
                returns = (returns == -1) ? 1 : ++returns;

                ReadRequest balanceRequest = ReadRequest.newBuilder().setName(userName + USER_BALANCE).build();
                ReadResponse balanceResponse = _recQuorum.read(balanceRequest);
                int balance = balanceResponse.getValue();
                int balanceSequence = balanceResponse.getSequence() + 1;
                balance = (balance == -1) ? prize : balance+prize;

                ReadRequest availableBikesRequest = ReadRequest.newBuilder().setName(stationId + STATION_BIKES_AVAILABLE).build();
                ReadResponse availableBikesResponse = _recQuorum.read(availableBikesRequest);
                int availableBikes = availableBikesResponse.getValue() + 1;
                int availableBikesSequence = availableBikesResponse.getSequence() + 1;

                WriteRequest newReturnsRequest = WriteRequest.newBuilder().setName(stationId + STATION_RETURNS)
                  .setIntValue(returns).setSequence(returnsSequence).setCid(this._cid).build();
                _recQuorum.write(newReturnsRequest);
                WriteRequest newBalanceRequest = WriteRequest.newBuilder().setName(userName + USER_BALANCE)
                  .setIntValue(balance).setSequence(balanceSequence).setCid(this._cid).build();
                _recQuorum.write(newBalanceRequest);
                WriteRequest newAvailableBikesRequest = WriteRequest.newBuilder().setName(stationId + STATION_BIKES_AVAILABLE)
                  .setIntValue(availableBikes).setSequence(availableBikesSequence).setCid(this._cid).build();
                _recQuorum.write(newAvailableBikesRequest);

                response = BikeResponse.newBuilder().setStatus(OK).build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }


    public void sysStatus(SysStatusRequest request, StreamObserver<SysStatusResponse> responseObserver) {

        SysStatRequest recRequest = SysStatRequest.newBuilder().build();
        SysStatResponse recResponse = _rec.sysStat(recRequest);
        String recStatus = recResponse.getStatus();


        SysStatusResponse response = SysStatusResponse.newBuilder().setHubStatus("UP").setRecStatus(recStatus).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void ctrlInit(CtrlInitRequest request, StreamObserver<CtrlInitResponse> responseObserver) {
        String inputData = request.getInput();
        Boolean initRecOption = request.getRecInitOption();

        this.initData(inputData, initRecOption);

        CtrlInitResponse response = CtrlInitResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void ctrlClear(CtrlClearRequest request, StreamObserver<CtrlClearResponse> responseObserver) {

        ClearRequest request1 = ClearRequest.newBuilder().build();
        _recQuorum.clear(request1);

        data.clearAll();

        CtrlClearResponse response = CtrlClearResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private LinkedHashMap<String, Integer> orderedResults(HashMap<String, Integer> toOrder, int number) {

        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(toOrder.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // put data from sorted list to hashmap
        LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            if (temp.size() == number) {
                break;
            }
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }


    public int getDistance(double latitude, double longitude, double stationLat, double stationLong) {
        double dLat = Math.toRadians(stationLat - latitude);
        double dLong = Math.toRadians(stationLong - longitude);
        //formula
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLong / 2), 2) *
                        Math.cos(latitude) *
                        Math.cos(stationLat);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));

        return ((int) ((rad * c) * 1000)); //result in meters
    }

    public synchronized void initData(String inputData, boolean initRecOption) {
        if (inputData.isBlank()) {
            throw new IllegalArgumentException("Input is Blank!");
        }

        String[] lines = inputData.split("\n");
        String[] tokens;

        for (String line : lines) {
            tokens = line.split(",");
            if (tokens.length == 3 && !initRecOption) {
                data.addUser(tokens[0], tokens[1], tokens[2]);
            } else if (tokens.length == 3 && initRecOption) {
                data.addUser(tokens[0], tokens[1], tokens[2]);
                WriteRequest balanceRequest = WriteRequest.newBuilder().setName(tokens[0] + "/user/Balance").setIntValue(0).build();
                _recQuorum.write(balanceRequest);
            } else if (tokens.length == 7 && !initRecOption) {
                data.addStation(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
                        Double.parseDouble(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[6]));

                WriteRequest availableBikesRequest = WriteRequest.newBuilder().setName(tokens[1] + "/station/AvailableBikes").setIntValue(Integer.parseInt(tokens[5])).build();
                _recQuorum.write(availableBikesRequest);
            } else if (tokens.length == 7 && initRecOption) {
                data.addStation(tokens[0], tokens[1], Double.parseDouble(tokens[2]),
                        Double.parseDouble(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[6]));

                WriteRequest availableBikesRequest = WriteRequest.newBuilder().setName(tokens[1] + "/station/AvailableBikes").setIntValue(Integer.parseInt(tokens[5])).build();
                _recQuorum.write(availableBikesRequest);

                WriteRequest pickupsRequest = WriteRequest.newBuilder().setName(tokens[1] + "/station/Pickups").setIntValue(0).build();
                _recQuorum.write(pickupsRequest);

                WriteRequest returnsRequest = WriteRequest.newBuilder().setName(tokens[1] + "/station/Returns").setIntValue(0).build();
                _recQuorum.write(returnsRequest);
            }
        }
    }

    private final class CloseServer extends Thread {
        @Override
        public void run() {
            _rec.closeChannel();
            _recQuorum.closeChannel();
        }
    }
}
