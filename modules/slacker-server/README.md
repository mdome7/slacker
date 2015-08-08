# slacker-server


### Running the server
First prepare the executable JAR
```
mvn clean package
```

Then, run it and specify where the configuration file is located.

To run using maven:
```
mvn exec:java -Dconfig=src/main/dist/config.yaml
```

Or simply:
```
java -Dconfig=src/main/dist/config.yaml -jar target/slacker-server-1.0-SNAPSHOT-exec.jar
```

Parameters:
* config - (required) path to the configuration file
* maxThreads - (optional) maximum number of threads used for processing requests (default=5)
