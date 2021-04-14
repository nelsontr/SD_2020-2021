package pt.tecnico.bicloin.hub.data;

public class User {
  private String _id;
  private String _name;
  private String _phoneNumber;

  public User(String id, String name, String phoneNumber){
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

  public String getPhoneNumber(){
    return this._phoneNumber;
  }
}
