FROM openjdk:11
EXPOSE 8080
COPY /target/email.sender.jar /var/lib/docker/tmp/buildkit-mount4239747498/target/email.sender.jar
ENTRYPOINT ["java","-jar","email.sender.jar"]
ENV TZ Europe/Rome