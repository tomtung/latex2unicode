name := "latex2unicode"

organization := "com.github.tomtung"

scalaSource in Compile <<= baseDirectory(_ / "src")

libraryDependencies ++= Seq(
		"org.parboiled" % "parboiled-core" % "1.0.2",
		"org.parboiled" % "parboiled-scala" % "1.0.2"
)