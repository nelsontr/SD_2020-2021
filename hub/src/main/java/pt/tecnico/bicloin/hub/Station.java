package pt.tecnico.bicloin.hub;

public class Station {
  private String _name;
  private String _id;
  private double _lat;
  private double _long;
  private int _dockCapacity;
  private int _prize;


  public Station(String name, String id, double lat, double lg, int dockCapacity, int prize) {
    _name = name;
    _id = id;
    _lat = lat;
    _long = lg;
    _dockCapacity = dockCapacity;
    _prize = prize;
  }

  public synchronized String getName() {
    return this._name;
  }

  public synchronized String getId() {
    return this._id;
  }

  public synchronized double getLat() {
    return this._lat;
  }

  public synchronized double getLong() {
    return this._long;
  }

  public synchronized int getDockCapacity() {
    return this._dockCapacity;
  }

  public synchronized int getPrize() {
    return this._prize;
  }
}
