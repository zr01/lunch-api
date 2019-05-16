FROM openjdk:8-jdk-alpine

ENV VERSION 0.0.1-SNAPSHOT

RUN mkdir -p /opt/rezdy
COPY target/lunch-$VERSION.jar /opt/rezdy
ENTRYPOINT ["java", "-jar", "/opt/rezdy/lunch-$VERSION.jar"]