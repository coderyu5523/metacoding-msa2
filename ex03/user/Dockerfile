FROM gradle:8.5-jdk21
WORKDIR /app
COPY . .
RUN gradle bootJar -x test
COPY ./build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
