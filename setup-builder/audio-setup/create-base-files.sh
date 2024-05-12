
sudo mkdir /home/ubuntu/
sudo mkdir /home/ubuntu/audio/

sudo mkdir -p /home/ubuntu/audio/debug

sudo cp -f script/*.sh  /home/ubuntu/audio/

sudo cp -f src/main/webapp/audio/10240-audio-streams-0230000.mp3  /home/ubuntu/audio/webrtc-source.mp3


cd /home/ubuntu/audio/

FFREPORT=file="webrtc-source.mp3.log":level=32 ffmpeg -i webrtc-source.mp3 -sample_fmt s16 -ar 10240 webrtc-source.wav

echo "done!"
