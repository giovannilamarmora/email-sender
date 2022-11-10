FROM openjdk:11
EXPOSE 8080
COPY /target/email.sender.jar email.sender.jar
ENTRYPOINT ["java","-jar","email.sender.jar"]
ENV TZ Europe/Rome