FROM ubuntu

RUN apt-get update && \
    apt-get install -y iputils-ping iproute2 openjdk-16-jdk-headless 

RUN apt-get install -y apache2 && \
	sed -i'' 's/\/var\/www\/html/\/test/' /etc/apache2/sites-available/000-default.conf && \
	sed -i'' 's/\/var\/www/\/test/' /etc/apache2/apache2.conf


WORKDIR /test

COPY bin .

ADD test.html index.html
ADD earth.jpg .

