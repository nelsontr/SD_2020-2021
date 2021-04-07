package pt.tecnico.hub;

import java.util.*;

public class Hub {
  private Map<String, User> _users = new HashMap<>();
  private Map<String, Station> _stations = new HashMap<>();

  public void addUser(User user) {
    _users.put(user.getId(),user);
  }

  public User getUser(String id) {
    return this._users.get(id);
  }

  public void addStation(Station station) {
    _stations.put(station.getId(),station);
  }

  public Station getStation(String id) {
    return this._stations.get(id);
  }


}
