FROM adoptopenjdk:11-jre-hotspot
COPY ./target/coronavirus-tracker-0.0.1-SNAPSHOT.jar /coronavirus-tracker.jar
ENV PORT 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/coronavirus-tracker.jar"]
