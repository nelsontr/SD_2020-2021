package pt.tecnico.bicloin.app;

import com.google.protobuf.GeneratedMessageV3;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import pt.tecnico.bicloin.hub.HubFrontend;
package pt.tecnico.bicloin.hub.grpc.*;
import java.util.*;


public class App {

  String _host;
  String _port;
  String _userId;
  double _latitude;
  double _longitude;
  HashMap<String, float[]> _tags;
  HubFrontend _hub;

  public App(String host, String port, String userId, float latitude, float longitude){
    _host = host;
    _port = port;
    _userId = userId;
    _latitude = latitude;
    _longitude = longitude;
    _hub = new HubFrontend(host, port);
    _tags = = new HashMap<String, float[]>();
  }

  protected void setCoords(float lt, float lg){
    _latitude = lt;
    _longitude = lg;
  }

	protected static void ping(String message){
    try {
			_hub.ping(message);
		} catch (StatusRuntimeException e) {
			System.out.println("Caught exception with description: " + e.getStatus().getDescription());
		}
	}

	protected static void balance(){
		//TODO
	}
	protected static void top(){
		//TODO
	}
	protected static void tag(){
		//TODO
	}
	protected static void move(){
		//TODO
	}
	protected static void at(){
		//TODO
	}
	protected static void scan(int number){
		LocateStationRequest request = LocateStationRequest.newBuilder().setLat(_latitude).setLong(_longitude).setNumStations(number).build();
    LocateStationResponse response = _hub.locateStation(request);

    for (i = 0; i < response.getScanCount() ; i++){
      Scan scan = response.getScan(i);
      String stationId = scan.getStationId(i);
      String distance = Integer.toString(scan.getDistance(i);
      System.out.println(stationId + ", " + distance);
    }
	}

	protected static void info(String stationId){
    InfoStationRequest request = InfoStationRequest.newBuilder().setStationId(stationId).build();
    InfoStationResponse response = _hub.infoStation(request);

    String stationName = response.getName();
    String lt = Double.toString(response.getLat());
    String lg = Double.toString(response.getLong());
    String dockCapacity = Integer.toString(response.getDockCapacity());
    String prize = Integer.toString(response.getPrize());
    String availableBikes = Integer.toString(response.getAvailableBikes());
    String pickups = Integer.toString(response.getPickups());
    String retunrs = Integer.toString(response.getReturns());
    String link = "https://www.google.com/maps/place/"
    link = link + lt + "," + lg;

    System.out.println(stationName + ", lat" + lt  + ", " + lg + " long, " + dockCapacity + " docas, " + prize + " BIC prémio, " +
      availableBikes + " bicicletas, " + pickups + " levantamentos, " + returns + " devoluções, " + link);

  }


	protected static void bike_up(){
		//TODO
	}
	protected static void bike_down(){
		//TODO
	}
	protected static void sys_status(){
		//TODO
	}

}
