lazy val playJsonVersion = "2.6.8"
lazy val scalaMetaVersion = "1.8.0"
lazy val scalaMetaParadiseVersion = "3.0.0-M11"
lazy val scalaTestVersion = "3.0.4"

lazy val commonSettings = Seq(
  organization := "play-json-meta",
  version := "0.1",
  scalaVersion := "2.12.8",
  libraryDependencies ++= Seq(
    "com.typesafe.play" %% "play-json" % playJsonVersion,
    "org.scalameta" %% "scalameta" % scalaMetaVersion,
    "org.scalameta" %% "testkit" % scalaMetaVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  ),
  addCompilerPlugin("org.scalameta" % "paradise" % scalaMetaParadiseVersion cross CrossVersion.full)
)

lazy val scalaMetaCode = (project in file("."))
  .settings(commonSettings: _*)

lazy val examples = (project in file("examples"))
  .settings(commonSettings: _*)
  .dependsOn(scalaMetaCode)
