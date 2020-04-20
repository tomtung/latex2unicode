name := "latex2unicode"

organization := "com.github.tomtung"

version := "0.2.7"

scalaVersion := "2.12.11"

crossScalaVersions := Seq("2.11.12", "2.12.4")

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/tomtung/latex2unicode"))

pomIncludeRepository := { _ => false }

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "2.3.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (version.value.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomExtra := (
  <scm>
    <url>git@github.com:tomtung/latex2unicode.git</url>
    <connection>scm:git:git@github.com:tomtung/latex2unicode.git</connection>
  </scm>
    <developers>
      <developer>
        <id>tomtung</id>
        <name>Tom Dong</name>
        <url>http://tomtung.com</url>
      </developer>
    </developers>)

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Automatic-Module-Name" -> "com.github.tomtung.latex2unicode"
)
