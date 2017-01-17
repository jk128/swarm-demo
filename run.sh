#!/bin/sh

#TODO change from ip address to app alias in hosts which nginx will resolve using its loadbalancer. or use Atlas.
#change ip address below to app alias in hosts file which nginx resolves to
#or use env variable. same in other dockerfile please
/opt/consul/consul agent -client=0.0.0.0 -data-dir=/opt/consul/data/ -join 172.17.0.2 &

java -Dswarm.project.stage=$PROJECTSTAGE -jar swarm-demo-swarm.jar
