FROM ubuntu:22.04

COPY . /home/tuneurl/src
WORKDIR /home/tuneurl/src


RUN apt update
RUN apt -y install sudo

RUN sh setup-builder/build-requisites.sh

ENTRYPOINT ["/bin/bash"]