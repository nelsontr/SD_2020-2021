# Application


## Authors

Group T07

- 78631 Maria Sbrancia
- 90732 João Lopes
- 93743 Nelson Trindade


### Lead developer 

78631 Maria Sbrancia <https://git.rnl.tecnico.ulisboa.pt/ist178631>

### Contributors

93743 Nelson Trindade <https://git.rnl.tecnico.ulisboa.pt/ist193743>

90732 João Lopes <https://git.rnl.tecnico.ulisboa.pt/ist190732>


## About

This is a CLI (Command-Line Interface) application.


## Instructions for using Maven

To compile and run using _exec_ plugin:

```
mvn compile exec:java
```

To generate launch scripts for Windows and Linux
(the POM is configured to attach appassembler:assemble to the _install_ phase):

```
mvn install
```

To run using appassembler plugin on Linux:

```
cd target/appassembler/bin/
./app localhost 8081 joao +35191102030 38.7380 -9.3000
```

To run using appassembler plugin on Windows:

```
cd target\appassembler\bin\
app localhost 8081 joao +35191102030 38.7380 -9.3000
```


## To configure the Maven project in Eclipse

'File', 'Import...', 'Maven'-'Existing Maven Projects'

'Select root directory' and 'Browse' to the project base folder.

Check that the desired POM is selected and 'Finish'.


----

