FROM gradle:8.5-jdk21
WORKDIR /app
COPY . .
RUN gradle bootJar -x test
ENTRYPOINT ["java", "-jar", "build/libs/app.jar"]
