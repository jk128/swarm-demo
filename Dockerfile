#build as follows:
#docker build -t maxant/swarmdemo .
#
#run as follows:
#docker run -h swarmdemo --name swarmdemo -it -p 8081:8081 -e PROJECTSTAGE=dockerlaptop maxant/swarmdemo /bin/bash
#or
#docker run -h swarmdemo --name swarmdemo     -p 8081:8081 -e PROJECTSTAGE=dockerlaptop maxant/swarmdemo &

FROM centos:7.1.1503
MAINTAINER Ant Kutschera <ant.kutschera@gmail.com>

#get basic tools
RUN yum -y install wget
RUN yum -y install unzip

#install consul
RUN mkdir /opt/consul
WORKDIR /opt/consul/

RUN wget https://releases.hashicorp.com/consul/0.7.2/consul_0.7.2_linux_amd64.zip
RUN unzip consul*.zip
RUN rm consul*.zip

#install java
RUN yum -y install java-1.8.0-openjdk.x86_64

#copy application
RUN mkdir /opt/app
WORKDIR /opt/app/
COPY target/swarm-demo-swarm.jar .
COPY run.sh .
COPY truststore.jks .

#app: HTTP
EXPOSE 8081

#consul: HTTP, TCP, TCP, TCP, DNS, RPC
EXPOSE 8500 8300 8301 8302 8600 8400

WORKDIR /opt/app
CMD ./run.sh
