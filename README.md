[online demo]: http://latex2unicode.herokuapp.com/
[demo source]: https://github.com/tomtung/latex2unicode-demo
[PEG]: https://en.wikipedia.org/wiki/Parsing_expression_grammar
[fastparse]: http://www.lihaoyi.com/fastparse
[latex-to-unicode by ypsu]: https://github.com/ypsu/latex-to-unicode
[latex-to-unicode by vikhyat]: https://github.com/vikhyat/latex-to-unicode

# LaTeX2Unicode [![Build Status](https://travis-ci.org/tomtung/latex2unicode.svg?branch=master)](https://travis-ci.org/tomtung/latex2unicode)

LaTeX2Unicode translates LaTeX markup to human readable Unicode when possible. Here's an [online demo] that can be conveniently used to type in special characters. ([demo source])

Basic math notations are supported. For instance:

```
\because \t{AB} + \t{BC} \neq \t{AC}
\therefore \iint\sqrt[4]{\xi^{\theta+1}} - \frac 38 \le
\Sigma \zeta_i \\
\therefore \exists{x}\forall{y} x \in \^A
```

is converted to

> ∵ A͡B + B͡C ≠ A͡C ∴ ∬∜ξ̅ᶿ̅⁺̅¹̅ - ⅜ ≤ Σ ζᵢ

> ∴ ∃x∀y x ∈ Â

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
val unicode = LaTeX2Unicode.convert(latex)
println(unicode)
```

In Java:

```java
import com.github.tomtung.latex2unicode.LaTeX2Unicode;

String latex = "\\ss^2 + \\alpha_3 \n \\div \\frac{1}{3} = \\sqrt[3]{123}"
String unicode = LaTeX2Unicode.convert(latex)
System.out.println(unicode);
```


## Maven / SBT Dependency

To add dependency on LaTeX2Unicode, insert the following to your `pom.xml` file if you use Apache Maven:

```xml
<dependency>
    <groupId>com.github.tomtung</groupId>
    <artifactId>latex2unicode_2.12</artifactId>
    <version>0.2.2</version>
</dependency>
```

or add the following to your `build.sbt` file if you use sbt 0.11+:

```scala
libraryDependencies += "com.github.tomtung" %% "latex2unicode" % "0.2.2"
```
# Credits

LaTeX2Unicode is inspired by two similar projects, [latex-to-unicode by ypsu] \(written in Python\) and [latex-to-unicode by vikhyat] \(written in Ruby\).

LaTeX2Unicode is built on [fastparse], an fast and elegant [PEG] parsing framework.

# Licence

Apache License Version 2.0
