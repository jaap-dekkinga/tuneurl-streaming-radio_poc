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

function die(){
  echo ""
  echo "ERROR: $1"
  echo ""
  exit 0
}

function if_exists(){
  if [ ! -f $1 ]; then
    die "Missing file '$1'" ;
  fi
}

if_exists "/home/ubuntu/audio/webrtc-source.mp3" ;
if_exists "/home/ubuntu/audio/webrtc-source.wav" ;


# url=https://demo.streamguys1.com/tuneurl
# crc32=d24d176b, duration=11
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/d24d176b11.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/d24d176b.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/d24d176b11.wav

# url=https://webrtc.tmalbon.com/audio/audio-streams.mp3
# crc32=260bd85f, duration=11
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/260bd85f11.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/260bd85f.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/260bd85f11.wav

# url=https://webrtclocal.tmalbon.com/audio/audio-streams.mp3
# crc32=c74e32e6, duration=11
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/c74e32e611.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/c74e32e6.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/c74e32e611.wav

# url=https://local.tmalbon.com/audio/audio-streams.mp3
# crc32=eda5d841, duration=11
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/eda5d84111.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/eda5d841.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/eda5d84111.wav

# url=https://tmalbon-tuneurl-audio.s3.us-east-2.amazonaws.com/audio/audio-streams.mp3
# crc32=c818b1bb, duration=11
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/c818b1bb11.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/c818b1bb.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/c818b1bb11.wav

# url=https://demo.streamguys1.com/tuneurl
# crc32=977c457f, duration=30
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/977c457f30.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/977c457f.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/977c457f30.wav

# url=https://webrtc.tmalbon.com/audio/audio-streams.mp3
# crc32=633a8a4b, duration=30
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/633a8a4b30.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/633a8a4b.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/633a8a4b30.wav

# url=https://webrtclocal.tmalbon.com/audio/audio-streams.mp3
# crc32=827f60f2, duration=30
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/827f60f230.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/827f60f2.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/827f60f230.wav

# url=https://local.tmalbon.com/audio/audio-streams.mp3
# crc32=a8948a55, duration=30
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/a8948a5530.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/a8948a55.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/a8948a5530.wav

# url=https://tmalbon-tuneurl-audio.s3.us-east-2.amazonaws.com/audio/audio-streams.mp3
# crc32=8d29e3af, duration=30
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/8d29e3af30.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/8d29e3af.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/8d29e3af30.wav

# url=https://demo.streamguys1.com/tuneurl
# crc32=ea0bb13a, duration=60
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/ea0bb13a60.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/ea0bb13a.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/ea0bb13a60.wav

# url=https://webrtc.tmalbon.com/audio/audio-streams.mp3
# crc32=1e4d7e0e, duration=60
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/1e4d7e0e60.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/1e4d7e0e.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/1e4d7e0e60.wav

# url=https://webrtclocal.tmalbon.com/audio/audio-streams.mp3
# crc32=ff0894b7, duration=60
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/ff0894b760.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/ff0894b7.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/ff0894b760.wav

# url=https://local.tmalbon.com/audio/audio-streams.mp3
# crc32=d5e37e10, duration=60
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/d5e37e1060.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/d5e37e10.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/d5e37e1060.wav

# url=https://tmalbon-tuneurl-audio.s3.us-east-2.amazonaws.com/audio/audio-streams.mp3
# crc32=f05e17ea, duration=60
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/f05e17ea60.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/f05e17ea.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/f05e17ea60.wav

# url=https://demo.streamguys1.com/tuneurl
# crc32=515860fa, duration=240
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/515860fa240.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/515860fa.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/515860fa240.wav

# url=https://webrtc.tmalbon.com/audio/audio-streams.mp3
# crc32=7018d280, duration=240
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/7018d280240.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/7018d280.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/7018d280240.wav

# url=https://webrtclocal.tmalbon.com/audio/audio-streams.mp3
# crc32=c2449c42, duration=240
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/c2449c42240.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/c2449c42.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/c2449c42240.wav

# url=https://local.tmalbon.com/audio/audio-streams.mp3
# crc32=8adc41e3, duration=240
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/8adc41e3240.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/8adc41e3.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/8adc41e3240.wav

# url=https://tmalbon-tuneurl-audio.s3.us-east-2.amazonaws.com/audio/audio-streams.mp3
# crc32=d791e788, duration=240
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/d791e788240.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/d791e788.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/d791e788240.wav

# url=https://demo.streamguys1.com/tuneurl
# crc32=06c0ad4b, duration=320
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/06c0ad4b320.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/06c0ad4b.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/06c0ad4b320.wav

# url=https://webrtc.tmalbon.com/audio/audio-streams.mp3
# crc32=27801f31, duration=320
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/27801f31320.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/27801f31.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/27801f31320.wav

# url=https://webrtclocal.tmalbon.com/audio/audio-streams.mp3
# crc32=95dc51f3, duration=320
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/95dc51f3320.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/95dc51f3.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/95dc51f3320.wav

# url=https://local.tmalbon.com/audio/audio-streams.mp3
# crc32=dd448c52, duration=320
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/dd448c52320.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/dd448c52.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/dd448c52320.wav

# url=https://tmalbon-tuneurl-audio.s3.us-east-2.amazonaws.com/audio/audio-streams.mp3
# crc32=80092a39, duration=320
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/80092a39320.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/80092a39.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/80092a39320.wav

# url=https://demo.streamguys1.com/tuneurl
# crc32=323d0d22, duration=600
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/323d0d22600.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/323d0d22.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/323d0d22600.wav

# url=https://webrtc.tmalbon.com/audio/audio-streams.mp3
# crc32=137dbf58, duration=600
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/137dbf58600.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/137dbf58.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/137dbf58600.wav

# url=https://webrtclocal.tmalbon.com/audio/audio-streams.mp3
# crc32=a121f19a, duration=600
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/a121f19a600.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/a121f19a.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/a121f19a600.wav

# url=https://local.tmalbon.com/audio/audio-streams.mp3
# crc32=e9b92c3b, duration=600
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/e9b92c3b600.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/e9b92c3b.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/e9b92c3b600.wav

# url=https://tmalbon-tuneurl-audio.s3.us-east-2.amazonaws.com/audio/audio-streams.mp3
# crc32=b4f48a50, duration=600
cp -f /home/ubuntu/audio/webrtc-source.mp3  /home/ubuntu/audio/b4f48a50600.mp3
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/b4f48a50.wav
cp -f /home/ubuntu/audio/webrtc-source.wav  /home/ubuntu/audio/b4f48a50600.wav

ls -vlt /home/ubuntu/audio/*.mp3
 
du -sh /home/ubuntu/audio/
