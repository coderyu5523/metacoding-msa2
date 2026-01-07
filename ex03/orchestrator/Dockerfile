FROM gradle:8.5-jdk21

WORKDIR /var/current/app

COPY . .

RUN gradle clean build -x test \
 && find build/libs -name "*.jar" -type f | head -1 | xargs -I {} cp {} application.jar

ENTRYPOINT ["java", "-jar", "application.jar"]

