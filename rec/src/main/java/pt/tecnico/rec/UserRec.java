package pt.tecnico.rec;

public class UserRec {
  private String _id;
  private int _balance;
  private float _lat;
  private float _long;

  public BikeRec(String id, int balance){
    _id = id;
    _balance = balance;
  }

  public String getId() {
    return _id;
  }

  public int getBalance() {
    return _balance;
  }
}
