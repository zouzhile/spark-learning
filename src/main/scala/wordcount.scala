import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object WordCountDriver {

  def main(args: Array[String]) {
    println("### INPUT = " + args(0))
    println("### OUTPUT = " + args(1))

    val conf = new SparkConf().setAppName("WordCountSpark")
    val sc = new SparkContext(conf)

    val f = sc.textFile(args(0)) 
    val wc = f.flatMap(l => l.split("\\s+")).map(word => (word, 1)).reduceByKey( _ + _) 
    wc.saveAsTextFile(args(1))

    sc.stop()
  }
}
