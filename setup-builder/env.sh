#! /bin/bash

export DB_SCHEMA=poctuneurl
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/$DB_SCHEMA
export MYSQL_URL=jdbc:mysql://localhost:3306/$DB_SCHEMA
export SPRING_PROFILES_ACTIVE=production
export SPRING_PORT=8281
export PORT=8281

export JAVA_HOME=$(which java)

export M2_HOME=/opt/maven/apache-maven-3.8.8


export PATH=$JAVA_HOME/bin:$M2_HOME/bin:$PATH