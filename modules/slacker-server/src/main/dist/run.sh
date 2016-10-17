#!/bin/sh

# TODO: have config and pluginDir be parameterizable
INSTALL_DIR=`dirname $0`
BASE_DIR=$INSTALL_DIR
JAVA_OPTS=-Xmx1g

if [ $# -gt 0 ]; then
  BASE_DIR=$1
fi

LOG_DIR=$BASE_DIR/logs
CONF_FILE=$BASE_DIR/config.yaml
PLUGIN_DIR=$BASE_DIR/plugins

LOG_FILE=$LOG_DIR/slacker.log
MAX_THREADS=3

mkdir -p $LOG_DIR

echo "Staring Slacker..."
echo "Installation directory: $INSTALL_DIR"
echo "Using base directory  : $BASE_DIR"

nohup java $JAVA_OPTS \
  -cp "$INSTALL_DIR/lib/*" \
  -Dconfig=$CONF_FILE \
  -DpluginDir=$PLUGIN_DIR \
  -DmaxThreads=$MAX_THREADS \
  com.labs2160.slacker.server.MainServer > $LOG_FILE 2>&1 &
echo "Slacker started.  See $LOG_FILE"