package pt.tecnico.hub;

public class User {
  private String _id;
  private String _name;
  private int _phoneNumber;

  public User(String id, String name, int phoneNumber){
    _id = id;
    _name = name;
    _phoneNumber = phoneNumber;
  }

  public String getId(){
    return this._id;
  }

  public String getName(){
    return this._name;
  }

  public int getPhoneNumber(){
    return this._phoneNumber;
  }
}
