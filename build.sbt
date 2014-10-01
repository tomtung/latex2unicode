name := "latex2unicode"

organization := "com.github.tomtung"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.9.3", "2.10.4", "2.11.2")

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/tomtung/latex2unicode"))

libraryDependencies ++= Seq(
		"org.parboiled" % "parboiled-core" % "1.1.6",
		"org.parboiled" %% "parboiled-scala" % "1.1.6"
)

publishMavenStyle := true

publishArtifact in Test := false

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) 
    Some("snapshots" at nexus + "content/repositories/snapshots") 
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

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
