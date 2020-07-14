TASKSET=0xFFFFFFFF

[ -z "$1" ] || TASKSET=$1

taskset $TASKSET java -XX:+FlightRecorder -XX:+UseParallelGC -Dquarkus.datasource.url=jdbc:postgresql://localhost:5432/hello_world?loggerLevel=OFF\&disableColumnSanitiser=true\&assumeMinServerVersion=12\&sslmode=disable -Dquarkus.http.host=127.0.0.1 -Djava.lang.Integer.IntegerCache.high=10000 -Dvertx.disableHttpHeadersValidation=true -Dvertx.disableMetrics=true -Dvertx.disableH2c=true -Dvertx.disableWebsockets=true -Dvertx.flashPolicyHandler=false -Dvertx.threadChecks=false -Dvertx.disableContextTimings=true -Dvertx.disableTCCL=true -Dhibernate.allow_update_outside_transaction=true -Djboss.threads.eqe.statistics=false -Dquarkus.http.port=8092 -jar target/hibernate-reactive-routes-blocking-1.0-SNAPSHOT-runner.jar
