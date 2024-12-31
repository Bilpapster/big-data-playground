import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Task4GraphEdgeCounts {
  def main(args: Array[String]): Unit = {
    Logger.getRootLogger.setLevel(Level.OFF)

    val spark = SparkSession.builder()
      .appName("GraphEdgeCounts")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext

    val inputFile: RDD[String] = sc.textFile("input/web-Stanford.txt")

    // Remove heading comments
    val edges: RDD[String] = inputFile.filter(line => !line.startsWith("#"))

    // Split each line into (src, dest) tuples
    val edgePairs: RDD[(String, String)] = edges.map(line => {
      val parts = line.split("\t")
      (parts(0), parts(1))
    })

    val outgoingCounts: RDD[(String, Int)] = edgePairs.map { case (src, _) => (src, 1) }
      .reduceByKey(_ + _)

    val incomingCounts: RDD[(String, Int)] = edgePairs.map { case (_, dest) => (dest, 1) }
      .reduceByKey(_ + _)

    val top10Incoming: Array[(String, Int)] = incomingCounts
      .top(10)(Ordering[Int].on[(String, Int)](_._2))

    val top10Outgoing: Array[(String, Int)] = outgoingCounts
      .top(10)(Ordering[Int].on[(String, Int)](_._2))

    println("Top 10 nodes with the most incoming edges:")
    top10Incoming.foreach(println)

    println("Top 10 nodes with the most outgoing edges:")
    top10Outgoing.foreach(println)

    val degreeCounts: RDD[(String, Int)] = outgoingCounts.fullOuterJoin(incomingCounts)
      .mapValues {
        case (Some(out), Some(in)) => out + in
        case (Some(out), None) => out
        case (None, Some(in)) => in
        case (None, None) => 0
      }

    val mean: Double = degreeCounts.values.sum() / degreeCounts.count()

    val filteredDegreeCount = degreeCounts.filter{ case (_, degree) => degree >= mean }.count()

    println(s"Nodes with degree greater than or equal to the average degree ($mean): $filteredDegreeCount")

    spark.stop()
  }
}

