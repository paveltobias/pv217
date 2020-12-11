# PV217 -- Distance Learning Platform

For now, a rough spec draft/discussion is available [here](https://github.com/paveltobias/pv217/wiki/Project-Domain-Discussion).


### Dependencies:
JDK 11  
Maven 3.6.2+  
Docker


### Quickstart:  
##### Compile and run services
cd ping-service  
mvn clean compile quarkus:dev

cd email-service  
mvn clean compile quarkus:dev

Check http://0.0.0.0:8080/ping, you should receive "pong 1".  
Check http://0.0.0.0:8081/ping, you should receive "pong 2".