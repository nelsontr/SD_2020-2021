package pt.tecnico.bicloin.hub;

import java.util.*;
import static io.grpc.Status.INVALID_ARGUMENT;

public class Hub {
  private Map<String, User> _users = new HashMap<>();
  private Map<String, Station> _stations = new HashMap<>();

  public void addUser(String userId, String name, String phoneNumber) {
    User user = new User(userId, name, phoneNumber);

    _users.put(user.getId(),user);
  }

  public User getUser(String id) {
    return this._users.get(id);
  }

  public void addStation(String stationName, String id, double latitude, double longitude, int dockCapacity, int prize) {
    Station station = new Station(stationName, id, latitude, longitude, dockCapacity, prize);
    _stations.put(station.getId(),station);
  }

  public Station getStation(String id) {
    return this._stations.get(id);
  }

  public Map<String, Station> getStations() {
    return this._stations;
  }

  public void clearAll(){
    _users.clear();
    _stations.clear();
  }
}
