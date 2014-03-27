name := "BetLiMS"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

playScalaSettings

templatesImport ++= Seq( 
  "controllers.FormEncapsulators._",
  "controllers.Models._"
)
