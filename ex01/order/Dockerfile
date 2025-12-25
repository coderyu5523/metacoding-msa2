FROM gradle:8.5-jdk21

WORKDIR /var/current/app

COPY . .

RUN chmod +x gradlew \
 && ./gradlew clean build -x test --no-daemon \
 && cp build/libs/*.jar application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]



