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
#
# ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", command,
# 1=uniqueName,
# 2=outputFilename,
# 3=crc32,
# 4=rootDir,
# 5=url,
# 6=duration.toString(), 7=executionMode);

# 1=uniqueName
UNIQUENAME="$1"

# 2=outputFilename
OUTPUTFILENAME="$2"

# 3=crc32 value
FILENAME="$3"

# 4=Folder where to save the .wav file
TARGETDIR="$4"
RUN_SCRIPT="$TARGETDIR/mp3-to-wav.sh"

# 5=the Audio Stream URL
URL="$5"

# 6=duration
DURATION=$6

# 7=TRUE or FALSE
EXECUTIONMODE=$7

# echo "DEBUG: 1=$1 2=$2 3=$3 4=$4 5=$5 6=$6"
STATUS=0

# Initial audio stream
fiveSecondAudioUrl="$FILENAME.wav"

FILENAME_SUFFIX=$FILENAME""$DURATION

# Final audio stream
finalAudioStreamUrl="$FILENAME_SUFFIX.wav"

# audio stream save by wget
MP3_FILENAME=$FILENAME_SUFFIX.mp3
SOURCE_MP3_FILE="$TARGETDIR/$MP3_FILENAME"

# initial 5 seconds audio converted by ffmpeg
TEMP_WAVE_FILE="$TARGETDIR/$fiveSecondAudioUrl"

# final audio converted by ffmpeg
FINAL_WAVE_FILE="$TARGETDIR/$finalAudioStreamUrl"

############################################################################
# 2=error or NONE
# 1=final duration
function emit_status(){
  rm -f $OUTPUTFILENAME
  echo "FILENAME=$FILENAME" > $OUTPUTFILENAME
  echo "fiveSecondAudioUrl=$fiveSecondAudioUrl" >> $OUTPUTFILENAME
  echo "finalAudioStreamUrl=$finalAudioStreamUrl" >> $OUTPUTFILENAME
  echo "duration=$1" >> $OUTPUTFILENAME
  echo "status=$STATUS" >> $OUTPUTFILENAME
  echo "ERROR=$2" >> $OUTPUTFILENAME
  echo "" >> $OUTPUTFILENAME
}

############################################################################
# 2=wav
# 1=mp3
function convert_mp3(){
RW="$1"
WF="$2"
rm -f "$WF"
  if [ -f "$RW" ]; then
    ffmpeg -i "$RW"  -sample_fmt  s16  -ar  10240  "$WF" 1> /dev/null 2>&1
  fi
}

############################################################################
# 1=if RETURN don't exit
# 1=otherwise do the exit
function run_get_status(){
# Check if wget with an MP3 filename still running in the background
sockproc=$(ps -efww | grep "wget" | grep "$MP3_FILENAME" | awk '{print $2}')
  if [ "x$sockproc" = "x" ]; then
DURATION=0
STATUS=1
  fi

  if [ "$1" != "RETURN" ]; then
    emit_status $DURATION "NONE" ;
    exit 0
  fi
}

############################################################################
#
function generate_final_wavefile(){
XERROR=NONE
  if [ -f $SOURCE_MP3_FILE ]; then

    if [ ! -f $FINAL_WAVE_FILE ]; then
      convert_mp3 $SOURCE_MP3_FILE $FINAL_WAVE_FILE ;
      if [ -f $FINAL_WAVE_FILE ]; then
        echo "OLD - CREATED: $FINAL_WAVE_FILE"
      else
        XERROR="Missing file $FINAL_WAVE_FILE"
      fi
    fi

    if [ ! -f $TEMP_WAVE_FILE ]; then
      cp -f $FINAL_WAVE_FILE  $TEMP_WAVE_FILE
      echo "OLD - CREATED: $TEMP_WAVE_FILE"
    fi
STATUS=1

  else
    XERROR="Missing file $SOURCE_MP3_FILE"
  fi
  emit_status $DURATION "$XERROR" ;
  exit 0
}

case ${EXECUTIONMODE} in
  KILL)
    generate_final_wavefile ;
    exit 0

      ;;

  FALSE)
  # Is in Query mode?
  # At this stage, the caller is checking for the completion of the conversion

    # This MP3 must exist. It was created below
    if [ ! -f $SOURCE_MP3_FILE ]; then
      emit_status $DURATION "Missing file $SOURCE_MP3_FILE" ;
      exit 0
    fi

    # After five seconds, this wave file was created - so it must exist
    if [ ! -f $TEMP_WAVE_FILE ]; then
      emit_status $DURATION "Missing file $TEMP_WAVE_FILE" ;
      exit 0
    fi

    # Verify if the wget completed its conversion.
    generate_final_wavefile ;
    exit 0

      ;;

  TRUE)
    # At this stage, it is possible the MP3 already existed and this call is from a page refresh
    if [ -f $SOURCE_MP3_FILE ]; then

      generate_final_wavefile ;
      exit 0
    fi

    echo "NEW - CREATING: $SOURCE_MP3_FILE"
    rm -f $TEMP_WAVE_FILE
    rm -f $FINAL_WAVE_FILE

    # send_daemon_file_monitor $FILENAME $DURATION
    # wget -O $SOURCE_MP3_FILE "$URL" 1>/dev/null 2>&1 &
    bash $RUN_SCRIPT $MP3_FILENAME $DURATION "$URL" "$SOURCE_MP3_FILE" "$TEMP_WAVE_FILE" "$FINAL_WAVE_FILE" 1>/dev/null 2>&1 &

COUNTER=0
while : ; do
    sleep 1
COUNTER=$((COUNTER + 1))
    # echo "COUNTER=$COUNTER, DURATION=$DURATION"
    if [ "$COUNTER" = "5" ]; then
        break
    fi
    # Check if wget with an MP3 filename still running in the background
    sockproc=$(ps -efww | grep "wget" | grep "$MP3_FILENAME" | awk '{print $2}')
    if [ "x$sockproc" = "x" ]; then
        break
    fi
done ;

      echo "NEW - CREATED: $SOURCE_MP3_FILE"
      convert_mp3 $SOURCE_MP3_FILE $TEMP_WAVE_FILE ;
XERROR="Missing file $TEMP_WAVE_FILE"
      if [ -f $TEMP_WAVE_FILE ]; then
        echo "NEW - CREATED: $TEMP_WAVE_FILE"

        cp -f $TEMP_WAVE_FILE  $FINAL_WAVE_FILE
        echo "NEW - CREATED: $FINAL_WAVE_FILE"

XERROR=NONE
      fi
      emit_status $DURATION  "$XERROR" ;
      exit 0

      ;;

  *)
      emit_status $DURATION "Invalid EXECUTION MODE" ;
      ;;

esac ;

exit 0
