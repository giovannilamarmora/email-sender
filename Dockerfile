FROM maven:3.9.7-eclipse-temurin-22 AS build
COPY . .
RUN mvn clean package -DGCLOUD_PROJECT=access-sphere

FROM eclipse-temurin:22-jdk
EXPOSE 8080
WORKDIR /
COPY src/main/resources /app/resources
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