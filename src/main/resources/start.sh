#!/bin/bash
pid=`ps -ef | grep "psmgr-1.0-SNAPSHOT.jar" | grep -v grep | awk '{print $2}'`
if [ "$pid" != "" ];then
  echo "already start pid: $pid"
else
  `nohup java -jar psmgr-1.0-SNAPSHOT.jar >/dev/null 2>&1 &`
  pid=`ps -ef | grep "psmgr-1.0-SNAPSHOT.jar" | grep -v grep | awk '{print $2}'`
  echo $pid
fi
