#! /bin/bash

sudo -i

apt install -y build-essential autoconf libtool pkg-config git cmake ssh curl dbus-x11 --fix-missing
apt install -y net-tools

apt install -y ffmpeg
