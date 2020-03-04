# Use the official maven/Java 8 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.6.3-jdk-11 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Use official adoptopenjdk 11 image
# https://hub.docker.com/_/adoptopenjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM adoptopenjdk:11-jre-hotspot

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/coronavirus-tracker-*.jar /coronavirus-tracker.jar

# Service must listen to $PORT environment variable.
# This default value facilitates local development.
ENV PORT 8080

# Run the web service on container startup.
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/coronavirus-tracker.jar"]

# FROM adoptopenjdk:11-jre-hotspot
# COPY ./target/coronavirus-tracker-0.0.1-SNAPSHOT.jar /coronavirus-tracker.jar
# ENV PORT 8080
# CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=${PORT}","-jar","/coronavirus-tracker.jar"]
