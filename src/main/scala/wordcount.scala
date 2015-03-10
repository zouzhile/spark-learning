import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object WordCountDriver {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("WordCountSpark")
    val sc = new SparkContext(conf)

    val f = sc.textFile("hdfs://appearfear.corp.ne1.yahoo.com:9000/README.md") 
    val wc = f.flatMap(l => l.split("\\s+")).map(word => (word, 1)).reduceByKey( _ + _) 
    wc.saveAsTextFile("hdfs://appearfear.corp.ne1.yahoo.com:9000/wc.txt")

    sc.stop()
  }
}
