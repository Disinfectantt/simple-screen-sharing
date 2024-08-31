FROM eclipse-temurin:21-alpine
RUN mkdir /opt/app && \
    mkdir /opt/app/data
COPY ./build/libs/simple-screen-sharing-0.0.1-SNAPSHOT.jar /opt/app/simple-screen-sharing.jar
WORKDIR /opt/app
CMD ["java", "-jar", "simple-screen-sharing.jar"]