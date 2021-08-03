ThisBuild / versionScheme := Some("early-semver")

ThisBuild / organization := "com.github.tomtung"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/tomtung/latex2unicode"),
    "scm:git@github.com:tomtung/latex2unicode.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "tomtung",
    name  = "Tom Dong",
    email = "tom.tung.dyb@gmail.com",
    url   = url("https://github.com/tomtung/")
  )
)

ThisBuild / description := "Convert LaTeX markup to Unicode."

ThisBuild / licenses := List("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

ThisBuild / homepage := Some(url("https://github.com/tomtung/latex2unicode"))

ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / Test / publishArtifact := false

ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

ThisBuild / publishMavenStyle := true
