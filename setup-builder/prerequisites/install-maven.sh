#! /bin/bash

sudo rm /etc/profile.d/maven.sh

export JAVA_PATH=$(which java)

sudo cat >/etc/profile.d/maven.sh <<'EOL'
export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=$JAVA_HOME/bin:/opt/maven/apache-maven-3.8.8/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:.:/snap/bin
EOL

sudo chmod +x /etc/profile.d/maven.sh

export JAVA_HOME=/usr/lib/jvm/java-1.21.0-openjdk-amd64
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=$JAVA_HOME/bin:/opt/maven/apache-maven-3.8.8/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:.:/snap/bin

source /etc/profile.d/maven.sh
