name := "socrasocial"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "dnvriend at bintray" at "http://dl.bintray.com/dnvriend/maven"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.5" % Test,
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % Test,
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "1.1.5",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.1.0",
  "org.json4s" %% "json4s-jackson" % "3.3.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.0"


)