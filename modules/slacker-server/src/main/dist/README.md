# Slacker

## Concepts
*Collectors* receive external requests via different channels
(e.g. http, xmpp) or strategies.  They can then fire off certain "Actions"
and then forward the responses back to the sources.  Collectors can be designed
to be passive (listening) or active (polling - e.g. checking FTP sites).

*Actions* perform arbitrary tasks (e.g. check stock price, retrieve weather information)
and form responses to be delivered back to the collectors.

## Usage
### Running the application
Before starting slackr, make sure you have configured _config.yaml_ correctly.
To start, simply execute on the command-line:
```
./run.sh
```

### Developing plugins
Using Maven, include dependency on _com.labs2160.slacker:slacker-api_.
```
<dependency>
  <groupId>com.labs2160.slacker</groupId>
  <artifactId>slacker-api</artifactId>
  <version>${project.version}</version>
</dependency>
```
In order to develop a collector, implement the interface
```
com.labs2160.slacker.api.RequestCollector
```
In order to develop an action, implement the interface
```
com.labs2160.slacker.api.Action
```
