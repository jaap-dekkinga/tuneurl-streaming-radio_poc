#!/bin/bash
#
# BSD 3-Clause License
#
# Copyright (c) 2024, TuneURL Inc.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# 1. Redistributions of source code must retain the above copyright notice, this
#    list of conditions and the following disclaimer.
#
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
#
# 3. Neither the name of the copyright holder nor the names of its
#    contributors may be used to endorse or promote products derived from
#    this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
# SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
# OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#

# to prevent running this accidentally - input 1234 as password
XPASSWORD=$1

COUNTER=0
DURATION=600

AUDIO_STREAM_URL="https://stream.radiojar.com/vzv0nkgsw7uvv"

WAVE_DIR='/home/ubuntu/audio'
FILENAME='webrtc-source'

MP3_FILE="$WAVE_DIR/$FILENAME.mp3"
WAV_FILE="$WAVE_DIR/$FILENAME.wav"

function die(){
    echo ""
    echo "ERROR: $1"
    echo ""
    exit 0
}

function audio_to_wavefile(){
if [ -f $1 ]; then
    if [ ! -f $2 ]; then
        ffmpeg -i $1 -sample_fmt s16 -ar 10240 $2
    fi
else
    die "Missing file '$1'"
    exit 0
fi
}

function do_kill_wget(){
    sockproc=$(ps -efww | grep "$USER " | grep "wget" | grep "$FILENAME" | awk '{print $2}')
    if [ "x$sockproc" != "x" ]; then
        if [ "$1" = "kill" ]; then
            echo ""
            echo "$2: $COUNTER -9 $sockproc"
            kill -9 $sockproc
            echo ""
        fi
    fi
}

if [ "x$XPASSWORD" != "x1234" ]; then

    die "Access to $0 denied - Invalid password" ;

fi

if [ -f "$MP3_FILE" ]; then
    if [ ! -f "$WAV_FILE" ]; then
        audio_to_wavefile $MP3_FILE $WAV_FILE ;
    fi
    ls -vlt $MP3_FILE
    ls -vlt $WAV_FILE
    exit 0
fi

echo " To download: $MP3_FILE"
echo "Convert into: $WAV_FILE"

rm -f $WAV_FILE

# Ensure no wget is running
echo ""
do_kill_wget kill kill ;

# Run wget in the background
wget -O $MP3_FILE "$AUDIO_STREAM_URL" &
wgetpid=$!
echo "wget PID: $wgetpid"

while : ; do
    sleep 1
COUNTER=$((COUNTER + 1))
    if [ "$COUNTER" = "$DURATION" ]; then
        break
    fi
    # Check if wget is still there
    sockproc=$(ps -efww | grep "$USER " | grep "wget" | grep "$FILENAME" | awk '{print $2}')
    if [ "x$sockproc" = "x" ]; then
        break
    fi
done ;

echo ""
kill -9 $wgetpid
do_kill_wget kill kill ;
echo ""

audio_to_wavefile $MP3_FILE $WAV_FILE ;
ls -vlt $MP3_FILE
ls -vlt $WAV_FILE
