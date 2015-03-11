import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD 

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.regression.LabeledPoint

object LogisticRegressionDriver {

  // the format of the data is "label,features", where features are separated by a single empty space
  // 0.0,-0.19138793197590276 0.7834675900121327
  // 1.0,3.712420417753061 3.55967640829891
  // 0.0,-0.3173743619974614 0.9034702789806682
  // 1.0,4.759494447180777 3.407011867344781
  // 0.0,-0.7078607074437426 -0.7866705652344417
  def toLabeledPoint(line: String) : LabeledPoint = {
    val parts = line.split(',')
    val label = java.lang.Double.parseDouble(parts(0))
    val features = Vectors.dense(parts(1).trim().split(' ').map(java.lang.Double.parseDouble))
    LabeledPoint(label, features)
  }

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("LogisticRegressionSpark")
    val sc = new SparkContext(conf)
    
    // resolve params
    val training = sc.textFile(args(0)).map(toLabeledPoint(_))
    val test = sc.textFile(args(1)).map(toLabeledPoint(_))
    val numIterations = args(2).toInt // default 100
    val stepSize = args(3).toDouble // default 1
    val miniBatchFraction = args(4).toDouble // default 1.0
    val regParam = args(5).toDouble // default 0.1
    val output = args(6)
   
    // init algo 
    val algo = new LogisticRegressionWithSGD() 
    algo.optimizer
        .setNumIterations(numIterations)
        .setMiniBatchFraction(miniBatchFraction)
        .setStepSize(stepSize)
        .setRegParam(regParam)
    println("[Logistic Regression] algorithm initialized")

    // model training
    val model = algo.run(training) 
    val modelRDD = sc.parallelize(model.weights.toArray)
    val modelPath = output + "/model"
    modelRDD.saveAsObjectFile(modelPath) // modelRDD can be deserialized with sc.objectFile(modelPath)
    println("[Logistic Regression] Trained model saved to " + modelPath)  
    println("[Logistic Regression] Weights: " + model.weights)

    // predict over the test data
    model.clearThreshold()
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    scoreAndLabels.cache() 
    val scorePath = output + "/scores"
    scoreAndLabels.saveAsTextFile(scorePath)
    println("[Logistic Regression] Scoring result save to " + scorePath)
    
    // model evaluation
    val metrics = new BinaryClassificationMetrics(scoreAndLabels) 
    val aucROC = metrics.areaUnderROC()
    println("[Logistic Regression] Model aucROC = " + aucROC) 
    val rocPath = output + "/aucROC"
    metrics.roc().saveAsTextFile(rocPath) 
    println("[Logistic Regression] aucROC curve saved to " + rocPath)

    // stop spark context
    sc.stop()
    println("[Logistic Regression] Done !")
  }
}
