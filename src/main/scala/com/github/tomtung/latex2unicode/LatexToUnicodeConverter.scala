package com.github.tomtung.latex2unicode

import org.parboiled.scala.parserunners.RecoveringParseRunner

/**
 * A simple utility class that converts LaTeX markup to Unicode whenever possible.
 */
object LatexToUnicodeConverter {
  private val runner = RecoveringParseRunner(new LatexParser().Input)

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
