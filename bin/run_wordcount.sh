#!/usr/bin/env bash

# see spark application submit doc: http://spark.apache.org/docs/latest/submitting-applications.html 

# on a machine without HADOOP_CONF_DIR exported, you need to export it to the directory with the hadoop conf xml files
# export HADOOP_CONF_DIR=/home/gs/conf/current

# see how to configure yarn properties: http://spark.apache.org/docs/latest/running-on-yarn.html 
# spark configuration guide: http://spark.apache.org/docs/latest/configuration.html#dynamically-loading-spark-properties

export JAVA_HOME=/home/gs/java/jdk64/current

export HADOOP_YARN_HOME=$HADOOP_PREFIX
export HADOOP_PREFIX=/home/gs/hadoop/current
export HADOOP_CONF_DIR=/home/gs/conf/current

export SPARK_HOME=/homes/zouzhile/spark/yspark_root/share/spark/
export SPARK_JAR=$SPARK_HOME/lib/spark-assembly-1.1.0.1-hadoop0.23.9.jar
SPARK_CLASSPATH="${SPARK_CLASSPATH}:${HADOOP_PREFIX}/share/hadoop/common/hadoop-gpl-compression.jar:$(ls ${HADOOP_PREFIX}/share/hadoop/hdfs/lib/YahooDNSToSwitchMapping-*.jar):$(ls ${HADOOP_PREFIX}/share/hadoop/common/hadoop-common*.jar | grep -v test | grep -v source):$(ls ${HADOOP_PREFIX}/share/hadoop/common/lib/jetty-util*.jar)"

export SPARK_YARN_USER_ENV="MALLOC_ARENA_MAX=4"
export SPARK_YARN_USER_ENV="JAVA_HOME=$JAVA_HOME"
export SPARK_YARN_USER_ENV="JAVA_HOME=/home/gs/java/jdk64/current,LD_LIBRARY_PATH=/home/gs/hadoop/current/lib/native/Linux-amd64-64/"

$SPARK_HOME/bin/spark-submit --driver-class-path $SPARK_CLASSPATH --class WordCountDriver --master yarn --deploy-mode client --conf "spark.yarn.queue=apg_p7" $PWD/target/spark-examples-1.2.jar
#$SPARK_HOME/bin/spark-submit --class WordCountDriver --master yarn-client --conf "spark.yarn.queue=apg_p7" $PWD/target/spark-examples-1.2.jar
