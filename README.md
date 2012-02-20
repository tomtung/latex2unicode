[online demo]: http://latex2unicode.herokuapp.com/
[demo source]: https://github.com/tomtung/latex2unicode-demo
[PEG]: https://en.wikipedia.org/wiki/Parsing_expression_grammar
[parboiled]: https://github.com/sirthias/parboiled/wiki
[latex-to-unicode by ypsu]: https://github.com/ypsu/latex-to-unicode
[latex-to-unicode by vikhyat]: https://github.com/vikhyat/latex-to-unicode

# LaTeX2Unicode

LaTeX2Unicode translates LaTeX markup to human readable Unicode when possible. Here's an [online demo] that can be conveniently used to type in special characters. ([demo source])

Basic math notations are supported. For instance:

```
\because \t{AB} + \t{BC} \neq \t{AC}
\therefore \iint\sqrt[4]{\xi^{\theta + 1}
- \frac 38} \le \Sigma \zeta_i \\
\therefore \exists{x}\forall{y} x \in \^A
```

is converted to

> ∵ A͡B + B͡C ≠ A͡C ∴ ∬∜(ξᶿ ⁺ ¹ - ⅜) ≤ Σ ζᵢ
> ∴ ∃x∀y x ∈ Â

Hundreds of other symbols and special characters are supported, too. For example, `\spadesuit`, `\aleph`, `\OE`, `\downdownarrows` and `\o` are translated to `♠`, `ℵ`, `Œ`, `⇊`, `ø`, respectively.

Some font styles are supported, too. For instance:

```
\textbb{Black Board Bold}, \textfrak{Fraktur},
{\bf Bold Face}, {\cal Calligraphic}, {\it Italic},
{\tt Monospace}
```

is translated to

> 𝔹𝕝𝕒𝕔𝕜 𝔹𝕠𝕒𝕣𝕕 𝔹𝕠𝕝𝕕, 𝔉𝔯𝔞𝔨𝔱𝔲𝔯, 𝐁𝐨𝐥𝐝 𝐅𝐚𝐜𝐞, 𝓒𝓪𝓵𝓵𝓲𝓰𝓻𝓪𝓹𝓱𝓲𝓬, 𝐼𝑡𝑎𝑙𝑖𝑐, 𝙼𝚘𝚗𝚘𝚜𝚙𝚊𝚌𝚎

# Using as Scala / Java Library

LaTeX2Unicode is written in Scala, thus can serve as a 3rd party library in any JVM application that needs to extract information from LaTeX texts (e.g. BibTeX).

## Simple Conversion

For simple conversion without configuration, which works fine in most cases, one call to a static method and you're done.

In Scala:

```scala
import com.github.tomtung.latex2unicode._

val latex = "\\ss^2 + \\alpha_3 \n \\div \\frac{1}{3} = \\sqrt[3]{123}"
val unicode = LatexToUnicodeConverter.convert(latex)
println(unicode)
```

In Java:

```java
import com.github.tomtung.latex2unicode.DefaultLatexToUnicodeConverter;

String latex = "\\ss^2 + \\alpha_3 \n \\div \\frac{1}{3} = \\sqrt[3]{123}"
String unicode = DefaultLatexToUnicodeConverter.convert(latex)
System.out.println(unicode);
```

## Customization

Unicode2LaTeX defines a [PEG] to parse simple LaTeX markups, which stored in the `LatexParser` class:

```
Text ← (Expression / WhiteSpaces) Text?
WhiteSpaces ← Spaces / SpacesMultiNewLine
Expression ← CharLiteral / Group / Command
Group ←  '{}' / '{'  Text '}'
Command ← Escape / Unary / UnaryWithOption / Binary / Style / UnknownCommand
Unary ← UnaryName Expression
UnaryWithOption ← UnaryWithOptionName '[' Text ']' Expression
Binary ← BinaryName Expression Expression
Style ← StyleName Text
UnknownCommand ← UnknownCommandName
```
(Details like matching optional spaces are omitted here.)

### Customizing Translation Rules

The ways how each rule is translated are saved in a groups of methods, whose names start with the word `translate`. You can override them to customize translation.

For example, if you want to preserve all white space characters:

```scala
import com.github.tomtung.latex2unicode._

val parser = new LatexParser{
  override protected def translateSpaces(matched: String) = matched
  override protected def translateSpacesMultiNewLine(matched: String) = matched
}

val converter = new LatexToUnicodeConverter(parser)

val text = converter.convert("""
This        is a
test
""")

println(text)
```

Similarly, you can also override fields whose names end with the word `Names` (e.g. `escapeNames`, `unaryNames`) to customize how commands are matched, e.g. to add custom commands.

### Working with Parboiled

The `LatexParser` class extends the `org.parboiled.matchers.Parser` class, which is part of the [parboiled] library. You can use `LatexParser` with parboiled to achieve more flexibility, e.g. customizing error handling strategy.

## Maven / SBT Dependency

To add dependency on LaTeX2Unicode, insert the following to your `pom.xml` file if you use Apache Maven:

```xml
<repositories>
	<!-- Other repositories ... -->
    <repository>
        <id>com.github.tomtung Snapshot</id>
        <url>http://tomtung.github.com/maven-repo/snapshots/</url>
    </repository>
</repositories>

<dependencies>
	<!-- Other dependencies ... -->
    <dependency>
        <groupId>com.github.tomtung</groupId>
        <artifactId>latex2unicode</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

or add the following to your `build.sbt` file if you use sbt 0.11+:

```scala
libraryDependencies += "com.github.tomtung" % "latex2unicode" % "0.1-SNAPSHOT"

resolvers += "com.github.tomtung Snapshot" at "http://tomtung.github.com/maven-repo/snapshots"
```
# Credits

LaTeX2Unicode is inspired by two similar projects, [latex-to-unicode by ypsu] \(written in Python\) and [latex-to-unicode by vikhyat] \(written in Ruby\).

LaTeX2Unicode is built on [parboiled], an elegant [PEG] parsing framework.

# Licence

Apache License Version 2.0