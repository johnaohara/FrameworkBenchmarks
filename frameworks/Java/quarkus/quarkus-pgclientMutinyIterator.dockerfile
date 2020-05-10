FROM maven:3.6.3-jdk-11-slim as maven
WORKDIR /quarkus
COPY pom.xml pom.xml
COPY base/pom.xml base/pom.xml
COPY hibernate/pom.xml hibernate/pom.xml
COPY pgclientMutiny/pom.xml  pgclientMutiny/pom.xml
COPY pgclientMutinyIterator/pom.xml  pgclientMutinyIterator/pom.xml
COPY pgclientReactiverse/pom.xml pgclientReactiverse/pom.xml
COPY pgclientReactiverseCustomPool/pom.xml pgclientReactiverseCustomPool/pom.xml
RUN mvn dependency:go-offline -q -pl base
COPY base/src base/src
COPY hibernate/src hibernate/src
COPY pgclient/src pgclient/src
COPY pgclientMutiny/src pgclientMutiny/src
COPY pgclientMutinyIterator/src pgclientMutinyIterator/src
COPY pgclientReactiverse/src pgclientReactiverse/src
COPY pgclientReactiverseCustomPool/src pgclientReactiverseCustomPool/src

RUN mvn package -q -pl  pgclientMutinyIterator -am

FROM openjdk:11.0.6-jdk-slim
WORKDIR /quarkus
COPY --from=maven /quarkus/ pgclientMutinyIterator/target/lib lib
COPY --from=maven /quarkus/ pgclientMutinyIterator/target/pgclient-mutiny-iterator-1.0-SNAPSHOT-runner.jar app.jar
CMD ["java", "-server", "-XX:+UseNUMA", "-XX:+UseParallelGC", "-Djava.lang.Integer.IntegerCache.high=10000", "-Dvertx.disableHttpHeadersValidation=true", "-Dvertx.disableMetrics=true", "-Dvertx.disableH2c=true", "-Dvertx.disableWebsockets=true", "-Dvertx.flashPolicyHandler=false", "-Dvertx.threadChecks=false", "-Dvertx.disableContextTimings=true", "-Dvertx.disableTCCL=true", "-Dhibernate.allow_update_outside_transaction=true", "-jar", "app.jar"]
