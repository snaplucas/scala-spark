import java.util.Random

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Usage: GroupByTest [numMappers] [numKVPairs] [KeySize] [numReducers]
  */
object GroupBy {
  def main(args: Array[String]) {
    val config = new SparkConf()
      .setMaster("local[2]")
      .setAppName("pi")

    val sc = new SparkContext(config)

    val numMappers = if (args.length > 0) args(0).toInt else 2
    val numKVPairs = if (args.length > 1) args(1).toInt else 1000
    val valSize = if (args.length > 2) args(2).toInt else 1000
    val numReducers = if (args.length > 3) args(3).toInt else numMappers

    val pairs1 = sc.parallelize(0 until numMappers, numMappers).flatMap { p =>
      val ranGen = new Random
      val arr1 = new Array[(Int, Array[Byte])](numKVPairs)
      for (i <- 0 until numKVPairs) {
        val byteArr = new Array[Byte](valSize)
        ranGen.nextBytes(byteArr)
        arr1(i) = (ranGen.nextInt(Int.MaxValue), byteArr)
      }
      arr1
    }.cache()
    // Enforce that everything has been calculated and in cache
    pairs1.count()

    println(pairs1.groupByKey(numReducers).count())

    sc.stop()
  }
}