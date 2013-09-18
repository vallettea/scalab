name := "Simple Project"

version := "1.0"

scalaVersion := "2.9.3"

libraryDependencies += "org.spark-project" %% "spark-core" % "0.7.3"

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "Spray Repository" at "http://repo.spray.cc/")