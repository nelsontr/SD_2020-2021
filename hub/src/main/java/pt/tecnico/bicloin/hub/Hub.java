package pt.tecnico.bicloin.hub;

import java.util.Map;
import java.util.HashMap;

public class Hub {
    private Map<String, User> _users = new HashMap<>();
    private Map<String, Station> _stations = new HashMap<>();

    private final static String NO_USER_ERROR = "Specified user doesn't exist";
    private final static String NO_STATION_ERROR = "Specified station doesn't exist";

    public void addUser(String userId, String name, String phoneNumber) {
        _users.put(userId, new User(userId, name, phoneNumber));
    }

    public User getUser(String id) {
        if (_users.containsKey(id))
            return this._users.get(id);
        throw new RuntimeException(NO_USER_ERROR);
    }

    public boolean existingUser(String id) {
        return _users.containsKey(id);
    }

    public Map<String, User> getUsers() {
        return this._users;
    }

    public void addStation(String stationName, String id, double latitude, double longitude, int dockCapacity, int prize) {
        _stations.put(id, new Station(stationName, id, latitude, longitude, dockCapacity, prize));
    }

    public Station getStation(String id) {
        if (_stations.containsKey(id))
            return this._stations.get(id);
        throw new RuntimeException(NO_STATION_ERROR);
    }

    public boolean existingStation(String id) {
        return _stations.containsKey(id);
    }

    public Map<String, Station> getStations() {
        return this._stations;
    }

    public void clearAll() {
        _users.clear();
        _stations.clear();
    }
}
