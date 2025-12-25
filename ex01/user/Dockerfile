FROM gradle:8.5-jdk21

WORKDIR /var/current/app

COPY . .

RUN chmod +x gradlew \
 && ./gradlew clean build -x test --no-daemon \
 && cp build/libs/*.jar application.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "application.jar"]



