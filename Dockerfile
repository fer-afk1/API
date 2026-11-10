FROM tomcat:9.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY dist/API_HealthBlood.war /usr/local/tomcat/webapps/API_HealthBlood.war
EXPOSE 8080
