FROM openjdk:8-jdk

WORKDIR /work
RUN wget 'http://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/bootstrap/target/BungeeCord.jar' -O BungeeCord.jar
COPY config.yml config.yml
CMD java -jar BungeeCord.jar