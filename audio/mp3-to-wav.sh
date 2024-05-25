#!/bin/bash
#
# BSD 3-Clause License
#
# Copyright (c) 2024, Teodoro M. Albon, <albonteddy@gmail.com>
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

XMP3_FILENAME=$1
XDURATION=$2
XURL="$3"
XSOURCE_MP3_FILE="$4"
XTEMP_WAVE_FILE="$5"
XFINAL_WAVE_FILE="$6"
COUNTER=0
wgetpid=0
IFEXIST=FALSE
IFKILL=FALSE

############################################################################
# 1=message
function die(){
    echo ""
    echo "ERROR: $1"
    echo ""
    exit 0
}

function ignore_the_error(){
    echo "Ignore the error: $XMP3_FILENAME, $COUNTER"
}

############################################################################
# 2=target wav file
# 1=mp3
function audio_to_wavefile(){
if [ -f $1 ]; then
    if [ ! -f $2 ]; then
        FFREPORT=file="$1.log":level=32 ffmpeg -i $1 -sample_fmt s16 -ar 10240 $2 1> /dev/null 2>&1
    fi
else
    die "Missing file '$1'"
    exit 0
fi
}

############################################################################
# If mp3 already exist, create the remaining wave file
function create_wave_files() {
  if [ -f $XSOURCE_MP3_FILE ]; then

      if [ ! -f $XFINAL_WAVE_FILE ]; then
          audio_to_wavefile  $XSOURCE_MP3_FILE  $XFINAL_WAVE_FILE
      fi

      # Initially the 5 second wav file is created by run_webrtc_script.sh
      rm -f $XTEMP_WAVE_FILE

      # Now make sure it is now the same with the finale wav file
      cp -f $XFINAL_WAVE_FILE  $XTEMP_WAVE_FILE

      # Conversion is done!
      exit 0
  fi
}

############################################################################
# 1=kill
# 2=name
function do_kill_wget(){
IFEXIST=FALSE
    sockproc=$(ps -efww | grep "$USER " | grep "wget" | grep "$XMP3_FILENAME" | awk '{print $2}')
    if [ "x$sockproc" != "x" ]; then
IFEXIST=TRUE
        if [ "$1" = "kill" ]; then
            echo ""
            echo "$2: $COUNTER -9 $sockproc"
            kill -9 $sockproc
            echo ""
        fi
    fi
}

############################################################################
# Check if wget with an MP3 filename still running in the background
do_kill_wget run run
if [ "$IFEXIST" = "TRUE" ]; then

    # wget is still creating the same mp3 file
    exit 0
fi

# wget is not running, but check if the mp3 file already exist
create_wave_files ;

############################################################################
# If MP3 does not exist, create it from the given Audio Stream
# Run wget in the background
wget -O $XSOURCE_MP3_FILE "$XURL" 1>/dev/null 2>&1 &
wgetpid=$!
echo "wget PID: $wgetpid"

IFKILL=TRUE
while : ; do
    sleep 1
COUNTER=$((COUNTER + 1))
    if [ "$COUNTER" = "$XDURATION" ]; then
        break
    fi
    # Check if wget is still there
    do_kill_wget run run
    if [ "$IFEXIST" = "FALSE" ]; then
IFKILL=FALSE
        break
    fi
done ;

echo ""
if [ "$IFKILL" = "TRUE" ]; then

    kill -9 $wgetpid || ignore_the_error ;

    # if the above call fail, kill it again

    do_kill_wget kill kill ;
    echo ""

fi

create_wave_files ;
