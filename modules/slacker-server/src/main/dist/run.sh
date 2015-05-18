#!/bin/sh
BASE_DIR=`dirname $0`
JAVA_OPTS=-Xmx1g
LOG_FILE=/var/log/slacker.log

CONF_FILE=$BASE_DIR/config.yaml
MAX_THREADS=3

echo "Staring Slacker..."
nohup java $JAVA_OPTS \
  -cp "./slacker-rs-exec-1.0-SNAPSHOT-exec.jar:$BASE_DIR/lib/*" \
  -Dconfig=$CONF_FILE \
  -DmaxThreads=$MAX_THREADS \
  com.labs2160.slacker.rs.exec.MainServer > $LOG_FILE 2>&1 &
echo "Slacker started.  See $LOG_FILE"