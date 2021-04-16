# T07-Bicloin

Distributed Systems 2020-2021, 2nd semester project


## Authors

**Group T07**


78631 Maria Sbrancia [maria.sbrancia@tecnico.ulisboa.pt](mailto:maria.sbrancia@tecnico.ulisboa.pt)

90732 João Lopes [joao.m.gaspar.lopes@tecnico.ulisboa.pt](mailto:joao.m.gaspar.lopes@tecnico.ulisboa.pt)

93743 Nelson Trindade [nelson.trindade@tecnico.ulisboa.pt](mailto:nelson.trindade@tecnico.ulisboa.pt)


### Module leaders

For each module, the README file must identify the lead developer and the contributors.
The leads should be evenly divided among the group members.

- **T1** - 93743 Nelson Trindade - https://git.rnl.tecnico.ulisboa.pt/ist193743
    - read, write procedures from rec and  hub-tester;
  
- **T2** - 90732 João Lopes - https://git.rnl.tecnico.ulisboa.pt/ist190732 
    - balance, top-up, bike-up and bike-down procedures from hub and  rec-tester;

- **T3** - 78631 Maria Sbrancia - https://git.rnl.tecnico.ulisboa.pt/ist178631
    - info_station, locate_station procedures from hub and app.

## Getting Started

The overall system is composed of multiple modules.

See the project statement for a full description of the domain and the system.

### Prerequisites

Java Developer Kit 11 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```

### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The integration tests are skipped because they require theservers to be running.


## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [gRPC](https://grpc.io/) - RPC framework


## Versioning

We use [SemVer](http://semver.org/) for versioning. 
