package pt.tecnico.rec;

public class Record {
    String _name;
    int _value;

    public Record(String name, int value) {
        _name = name;
        _value = value;
    }

    public String getName() {
        return _name;
    }

    public int getValue() {
        return _value;
    }

    public void setName(String name) {
        _name = name;
    }

    public void setValue(int value) {
        _value = value;
    }
}
