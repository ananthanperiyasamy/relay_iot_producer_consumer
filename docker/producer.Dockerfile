FROM openjdk:11-jdk-slim as builder

ARG JAR_FILE=iot-producer/target/iot-producer-0.0.1-SNAPSHOT.jar
EXPOSE 8083
ADD ${JAR_FILE} iot-producer.jar

ENTRYPOINT ["java","-jar","/iot-producer.jar"]