FROM openjdk:21-jdk

LABEL maintainer="lsh6451217@gmail.com"

VOLUME /tmp

EXPOSE 8081

ARG JAR_FILE=build/libs/flow-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} flow_assignment.jar

ENTRYPOINT ["java", "-jar", "/flow_assignment.jar"]