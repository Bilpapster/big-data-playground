import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkContext

import java.io.File
import scala.reflect.io.Directory

object Task1WordLength {
  def main(args: Array[String]): Unit = {
    Logger.getRootLogger.setLevel(Level.OFF)
    val sc = new SparkContext("local[*]", "SparkDemo")

    // define input-output paths and remove output path if exists, since spark goes crazy otherwise
    val INPUT_DIRECTORY = "input/SherlockHolmes.txt"
    val OUTPUT_DIRECTORY = "output/task1"
    new Directory(new File(OUTPUT_DIRECTORY)).deleteRecursively()


    // preprocess lines
    val lines = sc.textFile(INPUT_DIRECTORY).map(preprocessLine)

    // tokenize to words and remove empty tokens
    val words = lines.flatMap(line => line.split(' ')).filter(_.nonEmpty)

    // create a key-value pair in the form of (letter, (length, 1)) to calculate sum of lengths and frequency later
    val initialLetterToLengthPairRDD = words.map(word => (word.charAt(0), (word.length, 1)))

    // for each initial letter (key) sum up the total length and total frequency
    // then compute the mean length for each letter and sort
    initialLetterToLengthPairRDD.reduceByKey((x, y) => (x._1 + y._1, x._2 + y._2))
      .map(x => (x._1, x._2._1 / x._2._2.toDouble))
      .sortBy(letterToMeanLengthKV => letterToMeanLengthKV._2, ascending = false)
      .mapValues(roundToNDecimalPlaces(_, 2))
      .saveAsTextFile(OUTPUT_DIRECTORY)
    println(s"Task 2 completed successfully. Check the \"$OUTPUT_DIRECTORY\" directory for the execution results")
  }

  /**
   * Preprocesses a given string by transforming letters to lowercase and removing punctuation marks & digits.
   *
   * @param line the string to preprocess.
   * @return the preprocessed string.
   */
  private def preprocessLine(line: String): String = {
    line.toLowerCase().replaceAll("""[\p{Punct}\d]""", "")
  }

  /**
   * Rounds a decimal number to a specific decimal places' precision, specified by n.
   *
   * @param number the decimal number to round.
   * @param n      the number of decimal places in the result.
   * @return the rounded number.
   */
  private def roundToNDecimalPlaces(number: Double, n: Int): Double = {
    val factor: Double = Math.pow(10, n)
    (number * factor).round / factor
  }
}

