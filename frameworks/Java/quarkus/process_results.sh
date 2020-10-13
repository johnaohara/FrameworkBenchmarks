#!/bin/bash

INPUT_FILE=$1

DB_INTER_FILE="$1_db.tmp"
DB_OUTPUT_FILE="$1_db.csv"

QUERY_INTER_FILE="$1_query.tmp"
QUERY_OUTPUT_FILE="$1_query.csv"

#extract results into csv

#DB

#jq '.wrk.db | to_entries | .[].key as $runtime | .[].value | to_entries | .[].key as $latency | .[].value | to_entries | .[].key as $cores | .[].value | to_entries | .[].key as $eventLoops |. [].value | to_entries | .[].key as $dbConnections | .[].value| .["256"] | to_entries | .[] | .key as $loadLevel | [{ "runtime" : $runtime, "latency": $latency, "cores": $cores, "eventLoops": $eventLoops, "dbConnections" : $dbConnections,  "load" : $loadLevel, "meanLatency" : .value[0].meanLatency, "meanLatencyUnit": .value[0].meanLatencyUnit, "throughput" :.value[0].reqSec, "stdDevLatency" : .value[0].stdDevLatency, "stdDevLatencyUnit" : .value[0].stdDevLatencyUnit, "maxLatency" : .value[0].maxLatency, "maxLatencyUnit": .value[0].maxLatencyUnit, "50_centile": .value[0].latencyDistribution[0].latencyVal, "50_centile_unit": .value[0].latencyDistribution[0].latencyUnit, "75_centile": .value[0].latencyDistribution[1].latencyVal, "75_centile_unit": .value[0].latencyDistribution[1].latencyUnit, "90_centile": .value[0].latencyDistribution[2].latencyVal, "90_centile_unit": .value[0].latencyDistribution[2].latencyUnit, "99_centile": .value[0].latencyDistribution[3].latencyVal, "99_centile_unit": .value[0].latencyDistribution[3].latencyUnit, "99.9_centile": .value[0].latencyDistribution[4].latencyVal, "99.9_centile_unit": .value[0].latencyDistribution[4].latencyUnit, "99.99_centile": .value[0].latencyDistribution[5].latencyVal, "99.99_centile_unit": .value[0].latencyDistribution[5].latencyUnit, "99.999_centile": .value[0].latencyDistribution[6].latencyVal, "99.999_centile_unit": .value[0].latencyDistribution[6].latencyUnit,"100_centile": .value[0].latencyDistribution[7].latencyVal, "100_centile_unit": .value[0].latencyDistribution[7].latencyUnit}] ' results.json | less


jq '.wrk.db | to_entries | .[].key as $runtime | .[].value | to_entries | .[].key as $latency | .[].value | to_entries | .[].key as $cores | .[].value | to_entries | .[].key as $eventLoops |. [].value | to_entries | .[].key as $dbConnections | .[].value| .["256"] | to_entries | .[] | .key as $loadLevel | [$runtime, $latency, $cores, $eventLoops, $dbConnections, $loadLevel, .value[0].meanLatency, .value[0].meanLatencyUnit, .value[0].reqSec,  .value[0].stdDevLatency, .value[0].stdDevLatencyUnit, .value[0].maxLatency,  .value[0].maxLatencyUnit,  .value[0].latencyDistribution[0].latencyVal, .value[0].latencyDistribution[0].latencyUnit,  .value[0].latencyDistribution[1].latencyVal,  .value[0].latencyDistribution[1].latencyUnit,  .value[0].latencyDistribution[2].latencyVal,  .value[0].latencyDistribution[2].latencyUnit,  .value[0].latencyDistribution[3].latencyVal,  .value[0].latencyDistribution[3].latencyUnit,  .value[0].latencyDistribution[4].latencyVal,  .value[0].latencyDistribution[4].latencyUnit,  .value[0].latencyDistribution[5].latencyVal,  .value[0].latencyDistribution[5].latencyUnit,  .value[0].latencyDistribution[6].latencyVal,  .value[0].latencyDistribution[6].latencyUnit,.value[0].latencyDistribution[7].latencyVal,  .value[0].latencyDistribution[7].latencyUnit] | @csv ' $INPUT_FILE > $DB_INTER_FILE
#Query

jq '.wrk.update | to_entries | .[].key as $runtime | .[].value | to_entries | .[].key as $latency | .[].value | to_entries | .[].key as $cores | .[].value | to_entries | .[].key as $eventLoops |. [].value | to_entries | .[].key as $dbConnections | .[].value| .["256"] | to_entries | .[] | .key as $loadLevel | .value | to_entries | .[] | .key as $update | [$runtime, $latency, $cores, $eventLoops, $dbConnections, $loadLevel, $update, .value[0].meanLatency, .value[0].meanLatencyUnit, .value[0].reqSec,  .value[0].stdDevLatency, .value[0].stdDevLatencyUnit, .value[0].maxLatency,  .value[0].maxLatencyUnit,  .value[0].latencyDistribution[0].latencyVal, .value[0].latencyDistribution[0].latencyUnit,  .value[0].latencyDistribution[1].latencyVal,  .value[0].latencyDistribution[1].latencyUnit,  .value[0].latencyDistribution[2].latencyVal,  .value[0].latencyDistribution[2].latencyUnit,  .value[0].latencyDistribution[3].latencyVal,  .value[0].latencyDistribution[3].latencyUnit,  .value[0].latencyDistribution[4].latencyVal,  .value[0].latencyDistribution[4].latencyUnit,  .value[0].latencyDistribution[5].latencyVal,  .value[0].latencyDistribution[5].latencyUnit,  .value[0].latencyDistribution[6].latencyVal,  .value[0].latencyDistribution[6].latencyUnit,.value[0].latencyDistribution[7].latencyVal,  .value[0].latencyDistribution[7].latencyUnit]  | @csv' $INPUT_FILE > $QUERY_INTER_FILE

#clean .csv

cat $DB_INTER_FILE |  sed -e 's/\\"/''/g' | sed -e 's/\"/''/g' > $DB_OUTPUT_FILE
cat $QUERY_INTER_FILE |  sed -e 's/\\"/''/g' | sed -e 's/\"/''/g' > $QUERY_OUTPUT_FILE

