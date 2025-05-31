
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/bank-accounts-0.0.1-SNAPSHOT.jar bank-accounts.jar
ENTRYPOINT ["java", "-cp", "app.jar", "your.package.MainApplication"]
ENTRYPOINT ["java", "-jar", "bank-accounts.jar"]