#!/bin/bash
#
# Copyright (c) 2024 Teodoro M. Albon, <albonteddy@gmail.com> All rights reserved.
#
sockproc=$(ps -efww | grep "$USER " | grep "webrtc.util-1.1.1.jar" | grep "javax.net.ssl" | cut -c9-18)
if [ "x$sockproc" != "x" ]; then
	echo killing $sockproc ...
	kill -9 $sockproc
	echo "kill -9 ${sockproc}"
fi

sockproc=$(ps -efww | grep "$USER " | grep "webrtc.util-1.1.1.jar" | grep "javax.net.ssl" | cut -c9-18)
if [ "x$sockproc" != "x" ]; then
	echo killing $sockproc ...
	kill -9 $sockproc
	echo "kill -9 ${sockproc}"
fi
