FROM openjdk:21-jdk-slim

# ติดตั้ง bash
RUN apt-get update && apt-get install -y bash

WORKDIR /app

COPY . /app

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

RUN chmod +x wait-for-it.sh

RUN ./mvnw dependency:resolve

# package
RUN ./mvnw package -DskipTests 

# ใช้ bash แทน sh
ENTRYPOINT ["bash", "wait-for-it.sh", "my-mysql:3306", "--", "java", "-jar", "target/restaurant-0.0.1-SNAPSHOT.jar"]
