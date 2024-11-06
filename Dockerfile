FROM openjdk

WORKDIR /myapp

COPY target/SpringBootWebApp-1.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
