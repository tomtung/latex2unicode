package com.github.tomtung.latex2unicode

import org.parboiled.scala._

/**
 * A converter that converts LaTeX markup to Unicode.
 * @param parser Parser used for conversion.
 */
class LatexToUnicodeConverter(parser: LatexParser) {

  protected val runner: ParseRunner[String] = RecoveringParseRunner(parser.Input)

  /**
   * Converts LaTeX markup to Unicode whenever possible.
   * @param latex LaTeX markup
   * @return Resultant Unicode string
   */
  def convert(latex: String): String =
    try {
      runner.run(latex).result.getOrElse(latex)
    } catch {
      case _ => latex
    }
}

/**
 * Simple utility class that converts LaTeX markup to Unicode
 */
object LatexToUnicodeConverter extends LatexToUnicodeConverter(new LatexParser())