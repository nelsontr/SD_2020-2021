package pt.tecnico.rec;

import java.util.*;
import java.util.stream.Collectors;

public class Record {
    String _name;
    Object _value;

    public Record(String name, Object value) {
      _name = name;
      _value = value;
    }

    public String getName(){
      return _name;
    }
    public Object getValue(){
      return _value;
    }

    public void setName(String name){
      _name = name;
    }
    public void setValue(Object value){
      _value = value;
    }


}
