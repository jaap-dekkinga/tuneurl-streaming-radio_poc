#!/bin/bash
#
# Copyright (c) 2024 TuneURL Inc. All rights reserved.
#
export DB_SCHEMA=poctuneurl
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/$DB_SCHEMA
export MYSQL_URL=jdbc:mysql://localhost:3306/$DB_SCHEMA

export SPRING_PROFILES_ACTIVE=production
export SPRING_PORT=8281

# Seconds since 1970-01-01 00:00:00 UTC
dateVarTag=$(date +%s)

sockproc=$(ps -efww | grep "$USER " | grep "webrtc.util-1.1.1.jar" | grep "javax.net.ssl" | cut -c9-18)
if [ "x$sockproc" != "x" ]; then
  echo ""
  echo "*** run.sh already running ***"
  echo "Run ./kill.sh to terminate it!"
  echo ""
  exit 0
fi

if [ ! -f target/webrtc.util-1.1.1.jar ]; then
  mvn -Dmaven.test.skip=true clean package
  if [ ! -f target/webrtc.util-1.1.1.jar ]; then
    exit 0
  fi
fi

if [ -f ./boot.log ]; then
  mkdir -p ~/logs
  mv -f ./boot.log  ~/logs/tuneurl-$dateVarTag.log
fi

rm -rf /tmp/tomcat.8281*/
rm -rf /tmp/tomcat.8291*/

nohup java -jar target/webrtc.util-1.1.1.jar \
  -Djavax.net.ssl.trustStore=$JAVA_HOME/jre/lib/security/cacerts \
  -Djava.library.path=./jni:${LD_LIBRARY_PATH} \
  -Dmaven.test.skip=true > boot.log 2>&1 &

echo "To see SpringBoot logs, run as follows:"
echo "tail -n 1000 -f boot.log"
