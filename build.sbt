name := "latex2unicode"

version := "0.2.7"

scalaVersion := "2.12.14"

crossScalaVersions := Seq("2.11.12", "2.12.14")

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "1.0.0",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test"
)

Compile / packageBin / packageOptions += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "com.github.tomtung.latex2unicode"
)
