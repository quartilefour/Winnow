FROM openjdk:12
RUN mkdir /opt/app
ARG DEPENDENCY=/build
COPY ${DEPENDENCY}/libs/winnow-analyzer-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-jar", "/opt/app/winnow-analyzer-0.0.1-SNAPSHOT.jar"]