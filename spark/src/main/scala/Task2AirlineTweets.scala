import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

object Task2AirlineTweets {
  def main(args: Array[String]): Unit = {
    Logger.getRootLogger.setLevel(Level.OFF)

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
      println(s"Top 5 words for sentiment: $sentiment")
      cleanedData.filter($"airline_sentiment" === sentiment)
        .select($"cleaned_text")
        .as[String]
        .flatMap(_.split("\\s+"))
        .filter(_.nonEmpty)
        .groupBy("value")
        .count()
        .orderBy(desc("count"))
        .limit(5)
        .show()
    }

    val complaints = data
      .filter($"negativereason".isNotNull && !$"negativereason".equalTo(""))
      .filter($"negativereason_confidence" > 0.5)
      .groupBy($"airline", $"negativereason")
      .count()
      .withColumn("rank", row_number().over(Window.partitionBy($"airline").orderBy(desc("count"))))
      .filter($"rank" === 1)
      .select($"airline", $"negativereason", $"count")

    println("Top complaint reason per airline:")
    complaints.collect()
    complaints.show()

    spark.stop()
  }
}

