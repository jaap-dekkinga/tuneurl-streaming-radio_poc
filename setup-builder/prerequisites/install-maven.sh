#! /bin/bash

sudo rm /etc/profile.d/maven.sh

export JAVA_PATH=$(which java)

sudo cat >/etc/profile.d/maven.sh <<'EOL'
export JAVA_HOME=${JAVA_PATH}
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
EOL

sudo chmod +x /etc/profile.d/maven.sh

export JAVA_HOME=${JAVA_PATH}
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}

source /etc/profile.d/maven.sh
