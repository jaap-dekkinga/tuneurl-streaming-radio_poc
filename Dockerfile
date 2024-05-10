FROM ubuntu:latest

COPY . /home/tuneurl/src
WORKDIR /home/tuneurl/src

RUN setup-builder/config.sh

ENTRYPOINT ["bin", "bash"]