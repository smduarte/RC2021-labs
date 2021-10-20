FROM ubuntu

RUN apt-get update && \
    apt-get install -y iputils-ping iproute2 openjdk-16-jdk-headless 

RUN apt-get install -y apache2 && rm -rf /var/www/html  && ln -s /test /var/www/html


WORKDIR /test

COPY bin .

ADD test.html index.html
ADD earth.jpg .

