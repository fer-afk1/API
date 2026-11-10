FROM tomcat:9.0-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml
COPY dist/API_HealthBlood.war /usr/local/tomcat/webapps/API_HealthBlood.war
EXPOSE 8080