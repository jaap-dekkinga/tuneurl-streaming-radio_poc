### tuneurl-poc

Stream Radio POC - Java TuneUrl API server

Implementation of Swagger API here: `/src/main/webapp/v3/v3tuneurlpoc.json`

This is a Maven based project. It is a special version of my upwork gig project at [https://www.upwork.com/services/product/development-it-maven-based-api-service-built-with-java-and-spring-boot-1712920965715886080?ref=fl_profile](https://www.upwork.com/services/product/development-it-maven-based-api-service-built-with-java-and-spring-boot-1712920965715886080?ref=fl_profile)


### 1. Dependency

[How to install Apache2](https://ubuntu.com/server/docs/how-to-install-apache2)

[How To Install the Apache Web Server on Ubuntu 22.04](https://www.digitalocean.com/community/tutorials/how-to-install-the-apache-web-server-on-ubuntu-22-04)

[Java 1.8](https://www.oracle.com/ph/java/technologies/javase/javase8-archive-downloads.html)

[Maven 3.8.8](https://archive.apache.org/dist/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.tar.gz)

[com.albon.auth v1.0.1](lib/com.albon.auth.md)

[MySQL 8.0.36](https://dev.mysql.com/downloads/mysql/8.0.html)

[GNU Wget 1.21.2](https://www.gnu.org/software/wget/)

[ffmpeg version 4.4.2](https://www.ffmpeg.org/download.html)

### 2. Deployment on local machine with Ubuntu 22.04 LTS (it works on Ubuntu 20.04 LTS before)

#### 2.1 Install basic development tools

2. 1. Install java and maven and create `.bash_aliases` on the home folder (`/home/ubuntu/.bash_aliases` for user `ubuntu`)

2. 2. Sample `.bash_aliases` as follows:

```bash
#! /bin/bash

export DB_SCHEMA=poctuneurl
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/$DB_SCHEMA
export MYSQL_URL=jdbc:mysql://localhost:3306/$DB_SCHEMA
export SPRING_PROFILES_ACTIVE=production
export SPRING_PORT=8281
export PORT=8281

export JAVA_HOME=/opt/java/jdk1.8.0_231

export M2_HOME=/opt/maven/apache-maven-3.8.8

# Take a note about period inserted on PATH (E.g. "games:.:/snap"). It enable to run script on local path like:
#   `build.sh clean` instead of
# `./build.sh clean`
export PATH=$JAVA_HOME/bin:$M2_HOME/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:.:/snap/bin
```

3. Other build tools and utilities (E.g. wget and ffmpeg)

```bash

sudo -i

apt update
apt install -y build-essential autoconf libtool pkg-config git cmake ssh curl dbus-x11 --fix-missing
apt install -y net-tools
apt install -y apache2

# stop mysql first so we can setup correct password and database
apt install -y mysql-server mysql-client
systemctl stop mysql

apt install -y ffmpeg

exit

```

### 4. Setup initial poctuneurl database

#### 4.1 MySQL password setup

```bash
sudo -i

systemctl stop mysql
systemctl set-environment MYSQLD_OPTS="--skip-grant-tables --skip-networking"

systemctl start mysql
mysql -u root

mysql> USE mysql;
# Reading table information for completion of table and column names
# You can turn off this feature to get a quicker startup with -A
# Database changed
mysql> UPDATE user SET plugin='mysql_native_password' WHERE User='root';
# Query OK, 1 row affected (0.01 sec)
# Rows matched: 1  Changed: 1  Warnings: 0

mysql> FLUSH PRIVILEGES;
# Query OK, 0 rows affected (0.01 sec)

mysql> exit
# Bye

```

#### 4.2 Check MySQL password

```bash
sudo mysql -u root -p

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'passWord@123';
COMMIT ;
exit

sudo service mysql stop
sudo service mysql start

mysql --user=root --password=$PASSWORD

# mysql: [Warning] Using a password on the command line interface can be insecure.
# Welcome to the MySQL monitor.  Commands end with ; or \g.
# Your MySQL connection id is 9
# Server version: 8.0.36-0ubuntu0.22.04.1 (Ubuntu)
#
# Copyright (c) 2000, 2024, Oracle and/or its affiliates.
#
# Oracle is a registered trademark of Oracle Corporation and/or its
# affiliates. Other names may be trademarks of their respective
# owners.
#
# Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

mysql> exit

```

#### 4.3 Create poctuneurl database

```bash

cd db/

export PASSWORD=passWord@123

bash ./setup-db.sh install

```

### 5. Create audio folder

```bash

mkdir -p /home/ubuntu/audio/debug

cp -f script/*.sh  /home/ubuntu/audio/

```

### 6. Create webrtc-source.mp3

#### 6.1 Simply copy existing 10240-audio-streams-0230000.mp3 to webrtc-source.mp3

```bash
cp -f src/main/webapp/audio/10240-audio-streams-0230000.mp3  /home/ubuntu/audio/webrtc-source.mp3

```

#### 6.2 Or rebuild webrtc-source.mp3 from "https://demo.streamguys1.com/tuneurl"

6.2 Create `/home/ubuntu/audio/rebuild-webrtc-source.mp3.sh`

```bash
# This call will be completed in approximately 600 seconds or 10 minutes.

cd /home/ubuntu/audio/
bash ./rebuild-webrtc-source.mp3.sh  1234

```

### 7. Create webrtc-source.wav

```bash
cd /home/ubuntu/audio/

FFREPORT=file="webrtc-source.mp3.log":level=32 ffmpeg -i webrtc-source.mp3 -sample_fmt s16 -ar 10240 webrtc-source.wav

```

### 8. Rebuild the fingerprint module

8.1 Edit Makefile and replace **/opt/java/jdk1.8.0_231** with the full path of your Java installation

8.2 Run the following to create `/home/ubuntu/audio/fingerprintexec`

```bash

cd jni

make zap && make && make do-test && make zap

rm -f *.o
rm -f fingerprintexec
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/main.cpp -o main.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/ArrayCoord.cpp -o ArrayCoord.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/ArrayRankFloat.cpp -o ArrayRankFloat.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FastFourierTransform.cpp -o FastFourierTransform.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/Fingerprint.cpp -o Fingerprint.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FingerprintManager.cpp -o FingerprintManager.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FingerprintProperties.cpp -o FingerprintProperties.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/FingerprintSimilarityComputer.cpp -o FingerprintSimilarityComputer.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/MapRankInteger.cpp -o MapRankInteger.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/PairManager.cpp -o PairManager.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/QuickSortInteger.cpp -o QuickSortInteger.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/RobustIntensityProcessor.cpp -o RobustIntensityProcessor.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/Spectrogram.cpp -o Spectrogram.o
g++ -g2 -O2 -D__for_unix -D__ubuntu__ -shared -fPIC -I . -I ./cpp -I /opt/java/jdk1.8.0_231/include -I /opt/java/jdk1.8.0_231/include/linux -c cpp/WindowFunction.cpp -o WindowFunction.o
g++ -L /usr/lib -lm -lstdc++ -o fingerprintexec main.o ArrayCoord.o ArrayRankFloat.o FastFourierTransform.o Fingerprint.o FingerprintManager.o FingerprintProperties.o FingerprintSimilarityComputer.o MapRankInteger.o PairManager.o QuickSortInteger.o RobustIntensityProcessor.o Spectrogram.o WindowFunction.o 
chmod +x /home/ubuntu/audio/fingerprintexec
chmod +x /home/ubuntu/audio/runExternalFingerprintModule.sh
Test for jsExtractFingerprint
fingerprintexec fingerprint < fingerprint-test.txt > fingerprint-result.txt
Test for jsCompareFingerprint
fingerprintexec < fingerprint-comparison-test.txt
{"mostSimilarFramePosition":"-25","mostSimilarStartTime":"-0.775","score":"0.000158755","similarity":"0.000158755"}
rm -f *.o
rm -f fingerprintexec

```

### 9. Back-end configuration / settings

9.1 Install `lib/authsdk-1.0.1.jar`

Refer to [lib/com.albon.auth.md](lib/com.albon.auth.md)

9.2 Edit `src/main/resources/application.properties`

```bash
# Replace streamradiolocal.tmalbon.com with your domain

server.domain.url=https\://streamradiolocal.tmalbon.com/
audio.stream.url.prefix=https://streamradiolocal.tmalbon.com/dev/v3/audio

```

9.3 Edit `src/main/webapp/js/swagger-initializer.js`

```bash
# Replace streamradiolocal.tmalbon.com with your domain

    url: "https://streamradiolocal.tmalbon.com/v3/v3tuneurlpoc.json",

```

9.4 Edit `src/main/webapp/js/audio-demo.js`

```bash
# Replace streamradiolocal.tmalbon.com with your domain

const TEST_MP3_FILE = 'https://streamradiolocal.tmalbon.com/audio/10240-audio-streams-0230000.mp3';

const TRIGGERSOUND_AUDIO_URL = 'https://streamradiolocal.tmalbon.com/audio/10240-triggersound.wav';

        window.location.href = "https://streamradiolocal.tmalbon.com/";

    const res = await fetch("https://streamradiolocal.tmalbon.com/dev/v3/calculateFingerprint", {

            const res = await fetch("https://streamradiolocal.tmalbon.com/json/pretty-fingerprint-results-fingerprint1.json", {

    const res = await fetch(`https://streamradiolocal.tmalbon.com/dev/v3/evaluateOneSecondAudioStream?offset=${timeOffset}`, {

    const res = await fetch("https://streamradiolocal.tmalbon.com/dev/v3/evaluateAudioStream", {

    const res = await fetch(`https://streamradiolocal.tmalbon.com/dev/v3/getAudioStream?conversionId=${conversionId}`, {

        const res = await fetch("https://streamradiolocal.tmalbon.com/dev/v3/saveAudioStream", {

```

9.5 Edit `src/main/webapp/v3/v3tuneurlpoc.json`

```json
  // Replace streamradiolocal.tmalbon.com with your domain

  "servers": [
    {
      "url": "https://streamradiolocal.tmalbon.com",
      "description": "Stream Radio POC - Java TuneUrl API server URI"
    }
  ],
```

### 10. Build and run `target/webrtc.util-1.1.1.jar`

```bash
# NOTE: Make sure you have a valid poctuneurl database and installed lib/authsdk-1.0.1.jar
#
# See Step 4.3 Create poctuneurl database
# See Step 9.1 Install `lib/authsdk-1.0.1.jar`

cd ~/tuneurl-poc
./build.sh clean
./run.sh

# Exit from AWS EC2 instance to prevent your ssh session to hang. **Don't do the exit on your local dev**
exit

```

### 11. API is accessible at https://streaming.tuneurl-demo.com/v3/swagger/

<br>

![docs/TuneUrl-SwaggerUIforJavaTuneUrlAPIserver.png](docs/TuneUrl-SwaggerUIforJavaTuneUrlAPIserver.png)

<br>


### 12. TuneUrl-POC architecture

[TuneUrl-POC-architecture](docs/TuneUrl-POC-architecture-latest.pdf)


[About OneSecondAudioStreamController](docs/OneSecondAudioStreamController.md)
