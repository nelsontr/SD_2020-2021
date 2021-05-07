package pt.tecnico.bicloin.hub;

public class User {
  private String _id;
  private String _name;
  private String _phoneNumber;

  public User(String id, String name, String phoneNumber) {
    _id = id;
    _name = name;
    _phoneNumber = phoneNumber;
  }

  public synchronized String getId() {
    return this._id;
  }

  public synchronized String getName() {
    return this._name;
  }

  public synchronized String getPhoneNumber() {
    return this._phoneNumber;
  }
}
