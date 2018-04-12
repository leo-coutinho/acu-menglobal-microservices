FROM openjdk
MAINTAINER Leo Coutinho <theonlyleo@hotmail.com>
ADD target/gateway-service.jar gateway-service.jar
ENTRYPOINT ["java", "-jar", "/gateway-service.jar"]
EXPOSE 8765