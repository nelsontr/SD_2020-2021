# Rec - Server


## Authors

Group T07

- 78631 Maria Sbrancia
- 90732 João Lopes
- 93743 Nelson Trindade

### Lead developer 

93743 Nelson Trindade <https://git.rnl.tecnico.ulisboa.pt/ist193743>

### Contributors

78631 Maria Sbrancia <https://git.rnl.tecnico.ulisboa.pt/ist178631>

90732 João Lopes <https://git.rnl.tecnico.ulisboa.pt/ist190732>


## About

This is a gRPC server defined by the protobuf specification.

The server runs in a stand-alone process.

There are two types of data within the project, unchangeable data stored in Hub such as an id , and mutable data stored in Rec like a user's balance. Rec provides read and write operations to access ad modify the mutable records.

## Instructions for using Maven

To compile and run:

```
mvn compile exec:java
```

When running, the server awaits connections from clients.


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----

