package pt.tecnico.rec;

public class Record {
    private String _name;
    private int _value;
    private int _sequence;
    private int _cid;


    public Record(String name, int value, int sequence, int cid) {
        this._name = name;
        this._value = value;
        this._sequence = sequence;
        this._cid = cid;
    }

    public String getName() {
        return _name;
    }

    public int getValue() {
        return _value;
    }

    public int getSequence() {
        return _sequence;
    }

    public int getCid() {
        return _cid;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setValue(int value) {
        _value = value;
    }

    public void setSequence(int sequence) {
        _sequence = sequence;
    }

    public void setCid(int cid) {
        _cid = cid;
    }

    
}
