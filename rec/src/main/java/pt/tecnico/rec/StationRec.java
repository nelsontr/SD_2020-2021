package pt.tecnico.rec;

public class StationRec {
  private String _id;
  private int _dockCapacity;

  public StationRec(String id, int dockCapacity){
    _id = id;
    _dockCapacity = dockCapacity;
  }

  public String getId() {
    return _id;
  }

  public int getDockCapacity() {
    return _dockCapacity;
  }
}
