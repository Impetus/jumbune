#!/bin/bash
#This script deploys Jumbune, starts agent and jumbune container

#Fetching the version of Jumbune
JUMBUNE_T=$(ls /jumbune_code/distribution/target/ | grep jumbune)
JUMBUNE_VERSION=${JUMBUNE_T:13:-8}

#Deploying Jumbune
java -jar /jumbune_code/distribution/target/jumbune-dist-$JUMBUNE_VERSION-bin.jar -Ddistribution=a -DnamenodeIP=127.0.0.1 -Dusername=root -Dpassword=8InTHtRmX0dFyfI26mKX3Q==
nohup $JUMBUNE_HOME/bin/startWeb >/dev/null 2>&1 &
sleep 2
cd $JUMBUNE_HOME/agent-distribution/
#Starting the agent
nohup java -jar jumbune-remoting-$JUMBUNE_VERSION-agent.jar 5555 -verbose >/dev/null 2>&1 &
sleep 2
#Uploading Data onto the HDFS
$HADOOP_HOME/bin/hadoop fs -put $JUMBUNE_HOME/examples/resources/data/PREPROCESSED/data1  /data
sleep 2
#Starting History Server
$HADOOP_HOME/sbin/mr-jobhistory-daemon.sh start historyserver
sleep 2
#Adding Sample JSON to JSONRepo and changing to version of Jumbune in it
mkdir $JUMBUNE_HOME/jsonrepo
sed "s:1.5.0:$JUMBUNE_VERSION:" </root/sampleJson.json  >$JUMBUNE_HOME/jsonrepo/sampleJson.json
