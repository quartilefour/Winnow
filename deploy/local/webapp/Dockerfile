FROM tomcat:8.5-jre11-slim
RUN rm -rf /usr/local/tomcat/webapps/*
COPY /deploy/local/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh
RUN mkdir /code
WORKDIR /code
COPY /deploy/local/start-server.sh /code/
RUN chmod +x /wait-for-it.sh
RUN chmod +x /code/start-server.sh
ARG DEPENDENCY=/code/winnow/winnow-war/build
COPY ${DEPENDENCY}/libs/winnow-war-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
