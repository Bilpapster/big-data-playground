import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.{col, explode, lower, split}
import org.apache.spark.storage.StorageLevel

import java.io.File
import scala.reflect.io.Directory

object Task3MovieAnalytics {
  private val APP_NAME = "Movie Analytics"
  private val MASTER = "local[*]"
  private val INPUT_DIRECTORY = "input/movies.csv"
  private val OUTPUT_DIRECTORY = "output/task3"
  private val GENRE_ANALYTICS_SUBDIRECTORY = "moviesPerGenre"
  private val YEAR_ANALYTICS_SUBDIRECTORY = "moviesPerYear"
  private val TITLE_ANALYTICS_SUBDIRECTORY = "frequentTitleWords"
  private val TITLE_YEAR_ARRAY_TEMP_COLUMN = "title_year_array"
  private val YEAR_NOT_AVAILABLE_PLACEHOLDER = "N/A"

  def main(args: Array[String]): Unit = {
    Logger.getRootLogger.setLevel(Level.OFF)
    new Directory(new File(OUTPUT_DIRECTORY)).deleteRecursively()

    val rawDF = readDataFrame(INPUT_DIRECTORY) // READ RAW DATA
    val moviesDF = preprocessDataFrame(rawDF).persist(StorageLevel.MEMORY_AND_DISK) // CLEAN/PREPROCESS DATA
    computeGenreAnalytics(moviesDF) // COMPUTE GENRE ANALYTICS
    computeYearAnalytics(moviesDF) // COMPUTE YEAR ANALYTICS
    computeTitleAnalytics(moviesDF) // COMPUTE TITLE ANALYTICS

    println(s"Task 3 completed successfully. Check the \"$OUTPUT_DIRECTORY\" directory for the execution results")
  }

  /**
   * Reads data from the specified path in a Spark DataFrame.
   *
   * @param dataPath The path to read data from.
   * @return a DataFrame with the loaded data.
   */
  private def readDataFrame(dataPath: String): DataFrame = {
    SparkSession
      .builder().appName(this.APP_NAME).master(this.MASTER)
      .getOrCreate()
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .option("delimiter", ",")
      .csv(dataPath)
  }

  /**
   * Preprocesses the DataFrame with raw movie data by addressing data quality issues (e.g., missing year) and
   * extracting information to separate columns (e.g., separating title from year) to accommodate further analysis.
   *
   * @param rawDF The DataFrame to preprocess containing the raw movies' data.
   * @return A preprocessed DataFrame ready for executing analytics tasks on it.
   */
  private def preprocessDataFrame(rawDF: DataFrame): DataFrame = {
    def splitTitleYear: (String => Array[String]) = { str =>
      import scala.util.matching.Regex
      /*
       We are expecting a composite string that starts (^) with one or more characters (.*)
       followed by an opening parenthesis "\(", a valid year 1XXX or 2XXX ([1-2]\d{3}) and a closing parenthesis
       (the year regex here could have been more complex, e.g., ensuring after 1800 and before current year)
       followed by (optionally) some whitespaces "\s*" right before the string end ($).
       The first group we want to capture is the characters before the year (i.e., actual title) and the year itself,
       without the leading and trailing parentheses.
       */
      val compositeTitleYearRegex: Regex = """^(.*)\(([1-2]\d{3})\)\s*$""".r
      str match {
        case compositeTitleYearRegex(title, year) => Array(title.trim, year) // the string matches the regex
        case _ => Array(str.trim, YEAR_NOT_AVAILABLE_PLACEHOLDER) // the string is missing year -> fall-back to defaults
      }
    }

    val splitTitleYearUDF = org.apache.spark.sql.functions.udf(splitTitleYear)
    rawDF
      .withColumn(TITLE_YEAR_ARRAY_TEMP_COLUMN, splitTitleYearUDF(col("title")))
      .withColumn("title", col(TITLE_YEAR_ARRAY_TEMP_COLUMN)(0))
      .withColumn("year", col(TITLE_YEAR_ARRAY_TEMP_COLUMN)(1))
      .drop(col(TITLE_YEAR_ARRAY_TEMP_COLUMN))
      .withColumn("genres", split(col("genres"), """\|"""))
  }

  private def computeGenreAnalytics(moviesDF: DataFrame): Unit = {
    moviesDF
      .select(explode(col("genres")).as("genre"))
      .groupBy("genre").count.as("count")
      .sort("genre")
      .write
      .format("com.databricks.spark.csv")
      .option("delimiter", ",")
      .csv(OUTPUT_DIRECTORY + "/" + GENRE_ANALYTICS_SUBDIRECTORY)
  }

  private def computeYearAnalytics(moviesDF: DataFrame): Unit = {
    moviesDF
      .select("year")
      .groupBy("year").count.as("count")
      .sort(col("count").desc)
      .limit(10)
      .write.format("com.databricks.spark.csv").option("delimiter", ",")
      .csv(OUTPUT_DIRECTORY + "/" + YEAR_ANALYTICS_SUBDIRECTORY)
  }

  private def computeTitleAnalytics(moviesDF: DataFrame): Unit = {
    val removeSmallAndStopWords: (Row => Boolean) = { row =>
      row.getString(0).length >= 4 &&
        !row.getString(0).equalsIgnoreCase("with") &&
        !row.getString(0).equalsIgnoreCase("from") &&
        !row.getString(0).equalsIgnoreCase("(a.k.a.")
    }

    // Most frequent words (length >= 4) in titles
    moviesDF
      .select(explode(split(lower(col("title")), " ")).as("word"))
      .filter(removeSmallAndStopWords)
      .groupBy("word").count()
      .where(col("count") >= 10) // more like "having"
      .sort(col("count").desc)
      .write.format("com.databricks.spark.csv").option("delimiter", ": ")
      .csv(OUTPUT_DIRECTORY + "/" + TITLE_ANALYTICS_SUBDIRECTORY)
  }
}

