#!/usr/bin/env bash

# see spark application submit doc: http://spark.apache.org/docs/latest/submitting-applications.html 

# on a machine without HADOOP_CONF_DIR exported, you need to export it to the directory with the hadoop conf xml files
# export HADOOP_CONF_DIR=/home/gs/conf/current

# see how to configure yarn properties: http://spark.apache.org/docs/latest/running-on-yarn.html 
# spark configuration guide: http://spark.apache.org/docs/latest/configuration.html#dynamically-loading-spark-properties

export JAVA_HOME=/home/gs/java/jdk

export HADOOP_PREFIX=/home/gs/hadoop/current
export HADOOP_YARN_HOME=$HADOOP_PREFIX
export HADOOP_CONF_DIR=/home/gs/conf/current

export SPARK_BASE=/homes/zouzhile/spark/yspark_root
export SPARK_HOME=$SPARK_BASE/share/spark/
export SPARK_JAR=$SPARK_HOME/lib/spark-assembly-1.1.0.1-hadoop2.5.0.1.1408041624.jar
SPARK_CLASSPATH="${SPARK_CLASSPATH}:/home/gs/hadoop/current/share/hadoop/common/hadoop-gpl-compression.jar:$(ls ${HADOOP_PREFIX}/share/hadoop/hdfs/lib/YahooDNSToSwitchMapping-*.jar)"

$SPARK_HOME/bin/spark-submit --driver-class-path $SPARK_CLASSPATH --queue=apg_p7 --class WordCountDriver --master yarn --deploy-mode cluster $PWD/target/spark-examples-1.2.jar
#$SPARK_HOME/bin/spark-submit --class WordCountDriver --master yarn-client --conf "spark.yarn.queue=apg_p7" $PWD/target/spark-examples-1.2.jar
