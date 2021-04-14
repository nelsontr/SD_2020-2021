package pt.tecnico.bicloin.hub;

import java.util.*;
import static io.grpc.Status.INVALID_ARGUMENT;

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

  public Map<String, Station> getStations() {
    return this._stations;
  }


  public synchronized void ctrl_init_user(String input) {
      if (input.isBlank()) {
        throw new IllegalArgumentException("Input is Blank!");
      }

      String[] lines = input.split("\n");
      String[] tokens;

      for (String line : lines) {
        if (line.startsWith("#")) {
          continue;
        }

        tokens = line.split(",");
        if (tokens.length!=3){
          throw new IllegalArgumentException("Users must have 3 elements");
        }

        _users.put(tokens[0],new User(tokens[0], tokens[1], tokens[2]));
      }
  }

  public synchronized void ctrl_init_station(String input) {
    if (input.isBlank()) {
      throw new IllegalArgumentException("Input is Blank!");
    }

    String[] lines = input.split("\n");
    String[] tokens;

    for (String line : lines) {
      if (line.startsWith("#")) {
        continue;
      }

      tokens = line.split(",");
      if (tokens.length!=7){
        throw new IllegalArgumentException("Stations must have 7 elements");
      }

      _stations.put(tokens[1],new Station(tokens[0], tokens[1], Float.parseFloat(tokens[2]),
              Float.parseFloat(tokens[3]), Integer.parseInt(tokens[4]), Integer.parseInt(tokens[6])));
    }
  }
}
