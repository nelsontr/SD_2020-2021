package pt.tecnico.hub;

public class Station {
  private String _name;
  private String _id;
  private float _lat;
  private float _long;
  private int _dockCapacity;
  private int _prize;


  public Station(String name, String id, float lat, float lg, int dockCapacity, int prize){
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

  public float getLat(){
    return this._lat;
  }

  public float getLong(){
    return this._long;
  }

  public int getDockCapacity(){
    return this._dockCapacity;
  }

  public int getPrize(){
    return this._prize;
  }
}
