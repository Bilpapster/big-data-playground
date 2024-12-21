ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.11"

lazy val root = (project in file("."))
  .settings(
    name := "spark"
  )
libraryDependencies ++= Seq( "org.apache.spark" % "spark-core_2.11" % "2.1.0")

