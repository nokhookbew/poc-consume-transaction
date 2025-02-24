FROM openjdk:17.0.2-jdk-slim
VOLUME /tmp
COPY target/poc-consume-transaction-0.0.1-SNAPSHOT.jar poc-consume-transaction.jar
ENTRYPOINT ["java", "-jar", "poc-consume-transaction.jar"]