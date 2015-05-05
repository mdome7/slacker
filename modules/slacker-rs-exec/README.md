# slacker-rs-exec


### Running the server
First prepare the executable JAR
```
mvn clean package
```

Then, run it and specify where the configuration file is located.  For example:
```
java -Dconfig=modules/slacker-rs-exec/config.yaml -jar modules/slacker-rs-exec/target/slacker-rs-exec-1.0-SNAPSHOT-exec.jar
```

Command line parameters:
* config - (required) path to the configuration file
* maxThreads - (optional) maximum number of threads used for processing requests (default=5)
