FROM openjdk:11
RUN addgroup spring && adduser --ingroup spring spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} target/app.jar
COPY src/main/webapp src/main/webapp
ENTRYPOINT ["java","-XX:+UseG1GC","-Xms128m","-Xmx128m","-jar","target/app.jar"]