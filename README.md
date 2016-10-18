# Slacker

Slacker is a simple generic workflow engine that is easily extensible.

It can be used to quickly expose functionality via interfaces where "requests" can be submitted.
The request "collectors" can be implemented and exposed as a REST API, as chat bot (i.e. HipChat, Slack),
or an email reader (to name just a few examples).
Requests are then used as input to trigger pre-defined workflows wherein pluggable
"actions" are executed.  Actions perform work and generate output for other actions
to consume or can be delivered to the original collector and optional "endpoints". 

## Concepts
#### Collectors
*Collectors* receive external requests via different channels
(e.g. http, xmpp) or strategies.  They can then fire off certain _workflows_
and then forward the responses back to the sources.  Collectors can be designed
to be passive (listening) or active (polling - e.g. checking FTP sites).

#### Workflows
*Workflows* contain one or more _actions_ that are executed sequentially.
Each output of an _action_ can then be used by subsequent actions to produce another output.
At the end of the workflow, one or more endpoints can be configured where the last output is delivered.
The output is also delivered back to the original _collector_.

#### Actions
*Actions* perform arbitrary tasks (e.g. check stock price, retrieve weather information)
and form responses to be delivered back to the collectors.

#### Endpoints
*Endpoints* take the last response in the workflow and do something with them.
A workflow 

## Usage
### Running the application
Before starting slacker, make sure you have configured _config.yaml_ correctly.
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
