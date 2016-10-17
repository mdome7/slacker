# Slacker

## Usage
Unzip the package and execute _run.sh_.
Log output is sent to logs/slacker.log.

To test, point your browser to:
http://localhost:7000?request=echo%hello%20world

## Configure
Add plugin jars containing collectors and actions into the _lib_ folder.
Configure them accordingly in the _config.yaml_ file located in the base directory
of the unzipped package.

## Add plugins
Unzip plugins in the BASE_DIR/plugins directory

## Cool Stuff
curl http://localhost:7000/?request=stock%20aapl
curl http://localhost:7000/?request=weather%20seattle
curl http://localhost:7000?request=calc%207*191
curl http://localhost:7000?request=pick%202%20larry%20moe%20curly
