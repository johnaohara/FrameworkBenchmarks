java -Djava.lang.Integer.IntegerCache.high=4096 -Dvertx.disableHttpHeadersValidation=true -Dvertx.disableMetrics=true -Dvertx.disableH2c=true -Dvertx.disableWebsockets=true -Dvertx.flashPolicyHandler=false -Dvertx.threadChecks=false -Dvertx.disableContextTimings=true -Dvertx.disableTCCL=true -Dhibernate.allow_update_outside_transaction=true -jar target/benchmark-1.0-SNAPSHOT-runner.jar

