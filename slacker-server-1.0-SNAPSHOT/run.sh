#!/bin/sh
BASE_DIR=`dirname $0`
JAVA_OPTS=-Xmx1g
LOG_FILE=$BASE_DIR/logs/slacker.log

CONF_FILE=$BASE_DIR/config.yaml
MAX_THREADS=3

echo "Staring Slacker..."
nohup java $JAVA_OPTS \
  -cp "$BASE_DIR/lib/*" \
  -Dconfig=$CONF_FILE \
  -DmaxThreads=$MAX_THREADS \
  com.labs2160.slacker.server.MainServer > $LOG_FILE 2>&1 &
echo "Slacker started.  See $LOG_FILE"