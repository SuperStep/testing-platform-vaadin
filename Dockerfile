FROM openjdk:11
WORKDIR /
ADD target/testing-platform-vaadin-1.0.jar app.jar
RUN useradd -m myuser
USER myuser
EXPOSE 80
EXPOSE 8080
CMD java -jar -Dspring.profiles.active=prod app.jar