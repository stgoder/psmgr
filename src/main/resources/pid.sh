#!/bin/bash
pid=`ps -ef | grep "psmgr-1.0-SNAPSHOT.jar" | grep -v grep | awk '{print $2}'`
echo $pid
