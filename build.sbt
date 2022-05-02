name := "latex2unicode"

version := "0.3.2"
val scala211 = "2.11.12"
val scala212 = "2.12.15"
val scala213 = "2.13.8"

scalaVersion := scala212

crossScalaVersions := Seq(scala211,scala212,scala213)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "2.3.3",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "com.github.tomtung.latex2unicode"
)
