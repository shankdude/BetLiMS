name := "BetLiMS"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe.slick" %% "slick" % "2.0.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.play" %% "play-slick" % "0.6.0.1",
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.specs2" %% "specs2" % "2.3.11" % "test" 
)

playScalaSettings

templatesImport ++= Seq( 
  "controllers.FormEncapsulators._",
  "controllers.Models._"
)

routesImport += "controllers.Binders._"
