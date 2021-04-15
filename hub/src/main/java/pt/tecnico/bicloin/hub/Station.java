package pt.tecnico.bicloin.hub;

public class Station {
  private String _name;
  private String _id;
  private double _lat;
  private double _long;
  private int _dockCapacity;
  private int _prize;


  public Station(String name, String id, double lat, double lg, int dockCapacity, int prize){
    _name = name;
    _id = id;
    _lat = lat;
    _long = lg;
    _dockCapacity = dockCapacity;
    _prize = prize;
  }

  public String getName(){
    return this._name;
  }

  public String getId(){
    return this._id;
  }

  public double getLat(){
    return this._lat;
  }

  public double getLong(){
    return this._long;
  }

  public int getDockCapacity(){
    return this._dockCapacity;
  }

  public int getPrize(){
    return this._prize;
  }
}
