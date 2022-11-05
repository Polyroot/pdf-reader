FROM eclipse-temurin:17-jdk-jammy
VOLUME /tmp
RUN mkdir -p /home/parsed_files
ARG JAR_FILE=target/pdf-parser-0.1.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]