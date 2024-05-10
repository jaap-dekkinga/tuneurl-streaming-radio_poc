#! /bin/bash

cd /tmp/

wget https://downloads.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz

sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt

sudo ln -s /opt/apache-maven-3.8.8 /opt/maven

