organization := "fun.exercise"
name := "scala-exercise"
scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.2.9",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19",
  "com.vividsolutions" % "jts" % "1.13",
  "io.circe"          %% "circe-core" % "0.14.1",
  "io.circe"          %% "circe-generic" % "0.14.1",
  "io.circe"          %% "circe-parser" % "0.14.1"
)
