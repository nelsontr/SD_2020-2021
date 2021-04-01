package pt.tecnico.rec;

import java.util.*;
import java.util.stream.Collectors;

public class Record {

    Set<UserRec> _users;
    Set<StationRec> _stations;

    public Record() {
      _users = new HashSet<>();
      _stations = new HashSet<>();
    }

  
}
