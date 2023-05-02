FROM openjdk:8
LABEL authors="palla"
EXPOSE 8080
ADD target/imgur-gallery-api.jar imgur-gallery-api.jar

ENTRYPOINT ["java", "-jar", "/imgur-gallery-api.jar"]