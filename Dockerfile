FROM maven:3.8.2-jdk-11 AS build
COPY . .
RUN mvn clean package

FROM openjdk:11
EXPOSE 8080
WORKDIR /
COPY --from=build /target/email-sender.jar email-sender.jar

#ARG DEPLOY
#
#RUN if [ "$DEPLOY" = "STG" ]; then \
#    mv config/logback-stg.xml src/main/resources/logback.xml; \
#  fi \

#RUN if [ "$DEPLOY" = "STG" ]; then \
#    COPY /config/logback-stg.xml /src/main/resource/logback.xml \
#  elif  [ "$DEPLOY" = "PROD" ]; then \
#    COPY /config/logback-prod.xml /src/main/resource/logback.xml \
#  fi

ENTRYPOINT ["java","-jar","email-sender.jar"]
ENV TZ Europe/Rome