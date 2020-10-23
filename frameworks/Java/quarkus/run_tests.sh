#!/bin/bash
echo "$(date): Start"
DATE_STAMP=$(date +%Y_%m_%d_%H_%M)
export OUTPUT_DIR=/working/stats/hibernateReactive/scratch/$DATE_STAMP

TEST_DB=false
TEST_UPDATE=true

LOAD_LEVELS=$1

FIND_LIMITS=false
if [ $# -eq 2 ]
  then
    FIND_LIMITS=$2
fi

echo "Find limits: $FIND_LIMITS"

rm -Rf $OUTPUT_DIR
mkdir -p $OUTPUT_DIR/results
mkdir -p $OUTPUT_DIR/logs
CWD=$(pwd)

#DB_NETWORK_LATENCIES=(0ms 1ms)
DB_NETWORK_LATENCIES=(0ms)
#DB_CONNECTIONS=(1 2 4 8)
#DB_CONNECTIONS=(1 8)
DB_CONNECTIONS=(4)
#DB_CONNECTIONS=(1 4)
#CORES=(1 2)
CORES=(2)
CPU_MASKS=(0x00000001 0x00000003)
#CPU_MASKS=(0x00000003)
#EVENT_LOOP_MULTIPLIERS=(1 2)
EVENT_LOOP_MULTIPLIERS=(2)
#APPLICATIONS=(hibernate hibernate-reactive hibernate-reactive-routes-blocking pgclient)
APPLICATIONS=(hibernate hibernate-reactive)
#APPLICATIONS=(hibernate-reactive-routes-blocking pgclient)

APP_STATUS="["
export QUARKUS_HTTP_PORT=8182

#load levels
source $LOAD_LEVELS

function getDbConnectionCount(){
  ps -AF | grep "[b]enchmarkdbuser hello_world" | wc | awk '{ print $1}'
}

function getProcessEventLoops(){
  jstack $1 | grep "eventloop" | wc | awk '{print $1}'
}

function getAppLogErrors(){
  LOG_FILE=$1
  grep "[0-9]*:[0-9][0-9]:[0-9]*,[0-9]* ERROR" $LOG_FILE | wc | awk '{print $1}'
}

function genJsonRunResult(){
  app=$1
  dbCons=$2
  cores=$4
  eventLoops=$3
  latency=$5
  logFile=$6
  status=$7
  err=$8

  jq -n \
      --arg app "$app" \
      --arg dbCons "$dbCons" \
      --arg cores "$cores" \
      --arg eventLoops "$eventLoops" \
      --arg latency "$latency" \
      --arg logFile "$logFile" \
      --arg status "$status" \
      --argjson err "$err" \
      '{application: $app, databaseConnections: $dbCons, cores: $cores, eventLoops: $eventLoops, networkLatency: $latency, logFile: $logFile, status: $status, errors: $err}'
}

function genJsonError(){
  ERROR=$1
  jq -n \
      --arg error "$ERROR" \
      '{message: $error}'

}

function startDB(){
  echo "$(date): Starting database"
}

function loadDB(){
  PORT=$1
  NAME=$2
  IFS=',' read -r -a LOADS <<< "$3"
  OUTPUT=$4


  #CONNECTIONS=(128 256 512 1024)
  CONNECTIONS=(256)

  echo "$(date): Running DB Load: port; $PORT"
  echo "$(date): Running DB Warmup"
  #WARM-UP
  wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 20 -c 128 --timeout 8 -t 24  "http://localhost:$PORT/db" > /dev/null

  echo ""
  echo "$(date): Starting measurements"
  echo ""

  mkdir -p $OUTPUT

  for CONNECTION in $CONNECTIONS
  do
    if [[ "x$FIND_LIMITS" == "xtrue" ]]
    then
      FILENAME=$OUTPUT/$NAME.$CONNECTIONS.$load.wrk
      echo "$(date): Running DB:  $NAME (connections; $CONNECTIONS, load; $load). Writing to $FILENAME"
      ENDPOINT="http://localhost:$PORT/db"
      echo "Running load: wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24  $ENDPOINT > $FILENAME"
      wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24  $ENDPOINT > $FILENAME
    else
      for load in "${LOADS[@]}"
      do
          FILENAME=$OUTPUT/$NAME.$CONNECTIONS.$load.wrk2
          echo "$(date): Running DB:  $NAME (connections; $CONNECTIONS, load; $load). Writing to $FILENAME"
          ENDPOINT="http://localhost:$PORT/db"
          echo "Running load: wrk2 -R $load -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24  $ENDPOINT > $FILENAME"
          wrk2 -R $load -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24  $ENDPOINT > $FILENAME
      done
    fi
  done

  echo "$(date): Done DB Load"
}

function loadUpdates(){

  PORT=$1
  NAME=$2
  IFS=', ' read -r -a LOADS <<< "$3"
  OUTPUT=$4

  #CONNECTIONS=(128 256 512 1024)
  CONNECTIONS=(50)

#  QUERIES=(1 4 8)
  QUERIES=(1 8)

  echo "$(date): Running Update Load: port; $PORT"
  echo "$(date): Running Update Warmup"
  #WARM-UP
  wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 20 -c 128 --timeout 8 -t 24  "http://localhost:$PORT/updates?queries=1" > /dev/null

  echo ""
  echo "$(date): Starting measurements"
  echo ""

  mkdir -p $OUTPUT

  for CONNECTION in $CONNECTIONS
  do
    if [[ "x$FIND_LIMITS" == "xtrue" ]]
    then
      for QUERY in "${QUERIES[@]}"
      do
        FILENAME="$OUTPUT/$NAME.$CONNECTIONS.$load.$QUERY.wrk"
        echo "$(date): Running Updates:  $NAME (connections; $CONNECTIONS, load; $load, queries; $QUERY). Writing to $FILENAME"
        ENDPOINT="http://localhost:$PORT/updates?queries=$QUERY"
        echo "Running load: wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24 $ENDPOINT  > $FILENAME"
        wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24 $ENDPOINT  > $FILENAME
      done
    else
      for load in "${LOADS[@]}"
      do
          for QUERY in "${QUERIES[@]}"
          do
            FILENAME="$OUTPUT/$NAME.$CONNECTIONS.$load.$QUERY.wrk2"
            echo "$(date): Running Updates:  $NAME (connections; $CONNECTIONS, load; $load, queries; $QUERY). Writing to $FILENAME"
            ENDPOINT="http://localhost:$PORT/updates?queries=$QUERY"
            echo "Running load: wrk2 -R $load -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 24 $ENDPOINT  > $FILENAME"
            wrk2 -R $load -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c $CONNECTIONS --timeout 8 -t 10 $ENDPOINT  > $FILENAME
          done
      done
    fi


  done

  echo "$(date): Done Update Load"
}

function runTests(){
  echo "$(date): setup sudo..."
  sudo echo "$(date): done"

  echo "$(date): setup database..."

  sudo podman stop HibernateTestingPGSQL
  POD_ID=$(sudo podman run --ulimit memlock=-1:-1 -d --rm=true --memory-swappiness=0 --name HibernateTestingPGSQL -e POSTGRES_USER=benchmarkdbuser -e POSTGRES_PASSWORD=benchmarkdbpass -e POSTGRES_DB=hello_world -p 5432:5432 postgres:12)

  echo "$(date): populate database..."

  cd hibernate
  mvn clean compile quarkus:dev &
  DEV_PID=$!
  sleep 10s
  curl "http://localhost:$QUARKUS_HTTP_PORT/createdata"

  kill -15 $DEV_PID

  sleep 2s

  #rebuild
  mvn clean package

  cd $CWD

  echo "$(date): populate database - done"

  ITERATIONS=$(( ${#DB_NETWORK_LATENCIES[@]} * ${#DB_CONNECTIONS[@]} * ${#CORES[@]} * ${#EVENT_LOOP_MULTIPLIERS[@]} * ${#APPLICATIONS[@]} ))
  COUNTER=0

  for LATENCY in "${DB_NETWORK_LATENCIES[@]}"
  do
    sudo tc qdisc del dev cni-podman0 root
    echo "$(date): Setting network latency: $LATENCY"
    sudo tc qdisc add dev cni-podman0 root netem delay $LATENCY

    for CORE in "${CORES[@]}"
    do
      CPU_MASK="${CPU_MASKS[$CORE - 1]}"
      echo "$(date): Setting cores: $CORE with mask $CPU_MASK"
      for EVENT_LOOP_MULTIPLIER in "${EVENT_LOOP_MULTIPLIERS[@]}"
      do
        echo "Start event loop multiplier"
        EVENT_LOOPS=$(( CORE * EVENT_LOOP_MULTIPLIER ))
        echo "$(date): Setting Event Loops: $EVENT_LOOPS"

        for DATA_CONNECTION in "${DB_CONNECTIONS[@]}"
        do
          echo "$(date): Setting database connections: $DATA_CONNECTION"

          #Configure
          export QUARKUS_VERTX_EVENT_LOOPS_POOL_SIZE=$EVENT_LOOPS
          [[ "$DATA_CONNECTION" -lt "$EVENT_LOOPS" ]] && export REACTIVE_POOL_SIZE=1 || export REACTIVE_POOL_SIZE=$(( DATA_CONNECTION / EVENT_LOOPS ))
          export PGCLIENT_POOLSIZE=$DATA_CONNECTION
          export QUARKUS_DATASOURCE_MAX_SIZE=$DATA_CONNECTION
          export QUARKUS_DATASOURCE_MIN_SIZE=$DATA_CONNECTION
          export QUARKUS_DATASOURCE_INITIAL_SIZE=$DATA_CONNECTION
          export QUARKUS_DATASOURCE_REACTIVE_MAX_SIZE=$DATA_CONNECTION

          for APPLICATION in "${APPLICATIONS[@]}"
          do
            COUNTER=$((COUNTER + 1))

            echo "$(date): Starting application ( $COUNTER of $ITERATIONS ): $APPLICATION (DATA_CONNECTION: $DATA_CONNECTION, CORE: $CORE, EVENT_LOOPS: $EVENT_LOOPS, LATENCY: $LATENCY)"

            errors=()

            #start application
            cd $APPLICATION
            CONFIG=`cat java.conf`

            #rebuild
            mvn clean package

            RESULT_FILENAME="$APPLICATION.$LATENCY.cores-$CORE.eventloops-$EVENT_LOOPS.db-connections-$DATA_CONNECTION"
            LOG_FILE=$OUTPUT_DIR/logs/$RESULT_FILENAME.log

            echo "$(date): Application log file: $LOG_FILE"


            APP_CMD="taskset $CPU_MASK java $CONFIG -jar target/hibernate-runner.jar >> $LOG_FILE &"

            echo $(pwd) > $LOG_FILE
            env >> $LOG_FILE
            echo $APP_CMD >> $LOG_FILE
            echo "$(date): Application command: $APP_CMD"
            eval $APP_CMD
            APP_PID=$!
            echo "$(date): Application pid: $APP_PID"
            sleep 5s
            cd $CWD

            #pre-warm application
            wrk -H 'Host: localhost' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 10 -c 128 --timeout 8 -t 24  "http://localhost:$QUARKUS_HTTP_PORT/db" > /dev/null

            #test number of DB connections
            OPEN_DB_CONNECTIONS=$(getDbConnectionCount)

            echo "$(date): database connections - expected: $DATA_CONNECTION , actual: $OPEN_DB_CONNECTIONS"

            [[ $OPEN_DB_CONNECTIONS -eq $DATA_CONNECTION ]] || errors+=("DB connections: ERROR")

            #grep db log for errors
            #sudo podman container logs $POD_ID


            #grep application log for errors
            APPLICATION_LOG_ERROR=$(getAppLogErrors $LOG_FILE)

#            echo "$(date): application errors: $APPLICATION_LOG_ERROR"

            [[ $APPLICATION_LOG_ERROR -eq 0 ]] || errors+=("Application log: ERROR")

            #count event loops
            EVENTLOOPS=$(getProcessEventLoops $APP_PID )

            echo "$(date): event loops - expected: $EVENT_LOOPS , actual: $EVENTLOOPS"

            [[ $EVENTLOOPS -eq $EVENT_LOOPS ]] || errors+=("Application Event Loops: ERROR")


            #grep wrk output for errors

            STATUS="VALID"
            RUN_ERRORS="["

            for err in "${errors[@]}"
            do
              STATUS="INVALID"
               ERR_JSON=$(genJsonError "$err")
#               echo "Validation error: $err ($ERR_JSON)"
               RUN_ERRORS="${RUN_ERRORS} $ERR_JSON , "
            done

            RUN_ERRORS="${RUN_ERRORS% , } ]"

            appJson=$( genJsonRunResult $APPLICATION $DATA_CONNECTION $CORE $EVENT_LOOPS $LATENCY $LOG_FILE $STATUS "$RUN_ERRORS")

            APP_STATUS="$APP_STATUS $appJson , "

            #run tests
            LOAD_INDEX="$APPLICATION-$CORE-$LATENCY"
            DB_LOAD_LEVEL="${DB_LOAD[$LOAD_INDEX]}"

            if [[ "x$TEST_DB" == "xtrue" ]]
            then
              echo "$(date): Testing: $APPLICATION (db) with load: $DB_LOAD_LEVEL, output dir: $OUTPUT_DIR/results"
              loadDB $QUARKUS_HTTP_PORT "db.$RESULT_FILENAME" "$DB_LOAD_LEVEL" $OUTPUT_DIR/results
            fi

            if [[ "x$TEST_UPDATE" == "xtrue" ]]
            then
              QUERY_LOAD_LEVEL="${UPDATE_LOAD[$LOAD_INDEX]}"
              echo "$(date): Testing: $APPLICATION (update) with load: $QUERY_LOAD_LEVEL, output dir: $OUTPUT_DIR/results"
              loadUpdates $QUARKUS_HTTP_PORT "update.$RESULT_FILENAME" "$QUERY_LOAD_LEVEL" $OUTPUT_DIR/results
            fi

            kill -15 $APP_PID
            sleep 3s


          done #APPLICATION
        done #DATA_CONNECTION
      done #EVENT_LOOP_MULTIPLIER
    done #CORE
  done #LATENCY

  sudo tc qdisc del dev cni-podman0 root

  APP_STATUS="${APP_STATUS% , } ]"

  echo $APP_STATUS > $OUTPUT_DIR/status.json

  echo "$(date): Stopping database"
  sudo podman container stop $POD_ID
}

#start tests
runTests

echo "$(date): End"