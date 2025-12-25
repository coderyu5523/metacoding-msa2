FROM gradle:8.5-jdk21

WORKDIR /var/current/app

COPY . .

RUN sed -i 's/\r$//' gradlew \
 && chmod +x gradlew \
 && ./gradlew clean build -x test \
 && cp build/libs/*.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]