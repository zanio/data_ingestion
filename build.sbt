ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

val scalatestVersion = "3.2.11"
val scalatest        = "org.scalatest" %% "scalatest" % scalatestVersion % Test
lazy val KafkaAvroSerializerVersion = "5.3.0"

lazy val root = (project in file("."))
  .settings(
    name := "lunatech-aakash-choudhary",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.7.0-M1",
      "com.typesafe.akka" %% "akka-slf4j" % "2.7.0-M1",
      "com.datastax.oss" % "java-driver-core" % "4.15.0",
        scalatest
    )
  ).aggregate(consumer,producer,dataSource).dependsOn(consumer,producer)

lazy val producer = project
  .in(file("producer"))
  .settings(
    name := "producer",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.2.10",
      "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.6",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.8.1",
      "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.1",
      scalatest
    )
  ).dependsOn(dataSource)

lazy val consumer = project
  .in(file("consumer"))
  .settings(
    name := "consumer",
    libraryDependencies ++= Seq(
      "com.datastax.cassandra" % "cassandra-driver-core" % "4.0.0",
//      "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.6",
      scalatest
    )
  ).dependsOn(producer)


lazy val dataSource = project
  .in(file("dataSource"))
  .settings(
    name := "dataSource",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.2.10",
      "com.typesafe.akka" %% "akka-stream" % "2.7.0-M1",
      "io.circe" %% "circe-core" % "0.14.1",
      "io.circe" %% "circe-generic" % "0.14.1",
      "io.circe" %% "circe-parser" % "0.14.1"

    )


  )