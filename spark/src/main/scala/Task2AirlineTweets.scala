import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

import java.io.File
import scala.reflect.io.Directory

object Task2AirlineTweets {
  def main(args: Array[String]): Unit = {
    Logger.getRootLogger.setLevel(Level.OFF)

    val OUTPUT_DIRECTORY = "output/task2"
    new Directory(new File(OUTPUT_DIRECTORY)).deleteRecursively()

    val spark = SparkSession.builder()
      .appName("Airline Twitter Analysis")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val filePath = "input/tweets.csv"
    val data = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(filePath)

    val cleanedData = data.withColumn("cleaned_text",
      regexp_replace(lower($"text"), "[^a-zA-Z\\s]", "")
    )

    val sentiments = Seq("positive", "negative", "neutral")
    sentiments.foreach { sentiment =>
      val topWords = cleanedData.filter($"airline_sentiment" === sentiment)
        .select($"cleaned_text")
        .as[String]
        .flatMap(_.split("\\s+"))
        .filter(_.nonEmpty)
        .groupBy("value")
        .count()
        .orderBy(desc("count"))
        .limit(5)

      val sentimentOutputPath = s"output/task2/$sentiment-top-words"
      topWords.write
        .option("header", "true")
        .csv(sentimentOutputPath)
      println(s"Saved top 5 words for sentiment '$sentiment' to $sentimentOutputPath")
    }

    val complaints = data
      .filter($"negativereason".isNotNull && !$"negativereason".equalTo(""))
      .filter($"negativereason_confidence" > 0.5)
      .groupBy($"airline", $"negativereason")
      .count()
      .withColumn("rank", row_number().over(Window.partitionBy($"airline").orderBy(desc("count"))))
      .filter($"rank" === 1)
      .select($"airline", $"negativereason", $"count")

    val complaintsOutputPath = "output/task2/top-complaints"
    complaints.write
      .option("header", "true")
      .csv(complaintsOutputPath)
    println(s"Saved top complaint reasons to $complaintsOutputPath")

    spark.stop()
  }
}
