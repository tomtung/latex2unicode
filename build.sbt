name := "latex2unicode"

organization := "com.github.tomtung"

scalaSource in Compile <<= baseDirectory(_ / "src")

libraryDependencies ++= Seq(
		"org.parboiled" % "parboiled-core" % "1.0.2",
		"org.parboiled" % "parboiled-scala" % "1.0.2"
)

publishTo <<= (version) { version: String =>
      Some(Resolver.file("file", new File("./maven-repo") / {
        if  (version.trim.endsWith("SNAPSHOT"))  "snapshots"
        else                                     "releases/" }    ))
}
