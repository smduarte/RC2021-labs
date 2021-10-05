FROM ubuntu

RUN apt-get update && \
    apt-get install -y iputils-ping iproute2 openjdk-16-jdk-headless

WORKDIR /test

COPY bin/ /test/