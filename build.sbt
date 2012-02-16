name := "latex2unicode"

organization := "com.github.tomtung"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
		"org.parboiled" % "parboiled-core" % "1.0.2",
		"org.parboiled" % "parboiled-scala" % "1.0.2"
)

crossPaths := false

publishTo <<= (version) { version: String =>
      Some(Resolver.file("file", new File("./maven-repo") / {
        if  (version.trim.endsWith("SNAPSHOT"))  "snapshots"
        else                                     "releases/" }    ))
}
