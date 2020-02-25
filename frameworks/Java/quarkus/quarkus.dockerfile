FROM maven:3.6.1-jdk-11-slim as maven
WORKDIR /quarkus
RUN mkdir -p /root/.m2/repository/io
RUN mkdir -p /root/.m2/repository/org/jboss
COPY m2-quarkus /root/.m2/repository/io/quarkus
COPY m2-resteasy /root/.m2/repository/org/jboss/resteasy
COPY pom.xml pom.xml
RUN mvn dependency:go-offline
COPY src src
RUN mvn package

FROM openjdk:11.0.3-jdk-slim
WORKDIR /quarkus
COPY --from=maven /quarkus/target/lib lib
COPY --from=maven /quarkus/target/benchmark-1.0-SNAPSHOT-runner.jar app.jar
RUN wget https://github.com/jvm-profiling-tools/async-profiler/releases/download/v1.7-ea2/async-profiler-1.7-ea2-linux-x64.tar.gz && tar xvf async-profiler-1.7-ea2-linux-x64.tar.gz
RUN echo 1 > /proc/sys/kernel/perf_event_paranoid
RUN echo 0 > /proc/sys/kernel/kptr_restrict
CMD ["java", "-agentpath:/quarkus/async-profiler-1.7-ea2-linux-x64/build/libasyncProfiler.so=start,file=/logs/profile.svg",  "-server", "-XX:+UseNUMA", "-XX:+UseParallelGC", "-Djava.lang.Integer.IntegerCache.high=4096", "-Dvertx.disableHttpHeadersValidation=true", "-Dvertx.disableMetrics=true", "-Dvertx.disableH2c=true", "-Dvertx.disableWebsockets=true", "-Dvertx.flashPolicyHandler=false", "-Dvertx.threadChecks=false", "-Dvertx.disableContextTimings=true", "-Dvertx.disableTCCL=true", "-Dhibernate.allow_update_outside_transaction=true", "-jar", "app.jar"]
