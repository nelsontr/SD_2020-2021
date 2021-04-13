package pt.tecnico.bicloin.app;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import pt.tecnico.bicloin.hub.HubFrontend;
import pt.tecnico.bicloin.hub.grpc.*;
import java.util.*;


public class App {

  private String _host;
  private String _port;
  private String _userId;
  private double _latitude;
  private double _longitude;
  private Map<String, double[]> _tags;
  private HubFrontend _hub;

  public App(String host, String port, String userId, double latitude, double longitude){
    _host = host;
    _port = port;
    _userId = userId;
    _latitude = latitude;
    _longitude = longitude;
    _hub = new HubFrontend(host, port);
    _tags = new HashMap<String, double[]>();
    Runtime.getRuntime().addShutdownHook(new CloseServer());
  }

  void setCoords(float lt, float lg){
    _latitude = lt;
    _longitude = lg;
  }

	void ping(String message){
    try {
      CtrlPingRequest request = CtrlPingRequest.newBuilder().setInput(message).build();
			_hub.ping(request);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		}
	}

	void balance(){
		//TODO
	}
	void top(){
		//TODO
	}
	void tag(){
		//TODO
	}
	void move(){
		//TODO
	}
	void at(){
		//TODO
	}
	void scan(int number){
		LocateStationRequest request = LocateStationRequest.newBuilder().setLat(_latitude).setLong(_longitude).setNumStations(number).build();
    LocateStationResponse response = _hub.locateStation(request);

    for (int i = 0; i < response.getScanCount() ; i++){
      Scan scan = response.getScan(i);
      String stationId = scan.getStationId(i);
      String distance = Integer.toString(scan.getDistance(i));
      System.out.println(stationId + ", " + distance);
    }
	}

	void info(String stationId){
    InfoStationRequest request = InfoStationRequest.newBuilder().setStationId(stationId).build();
    InfoStationResponse response = _hub.infoStation(request);

    String stationName = response.getName();
    String lt = Double.toString(response.getLat());
    String lg = Double.toString(response.getLong());
    String dockCapacity = Integer.toString(response.getDockCapacity());
    String prize = Integer.toString(response.getPrize());
    String availableBikes = Integer.toString(response.getAvailableBikes());
    String pickups = Integer.toString(response.getPickups());
    String returns = Integer.toString(response.getReturns());
    String link = "https://www.google.com/maps/place/";
    link = link + lt + "," + lg;

    System.out.println(stationName + ", lat" + lt  + ", " + lg + " long, " + dockCapacity + " docas, " + prize + " BIC prémio, " +
      availableBikes + " bicicletas, " + pickups + " levantamentos, " + returns + " devoluções, " + link);

  }


	void bike_up(){
		//TODO
	}
	void bike_down(){
		//TODO
	}
	void sys_status(){
		//TODO
	}

  private final class CloseServer extends Thread {
    @Override
    public void run() {
      _hub.closeChannel();
    }
  }

}
