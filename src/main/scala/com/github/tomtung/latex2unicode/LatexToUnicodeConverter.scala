package com.github.tomtung.latex2unicode

import org.parboiled.scala.parserunners.RecoveringParseRunner

object LatexToUnicodeConverter {
  private val runner = RecoveringParseRunner(new LatexParser().Input)

  def convert(latex: String): String =
    try {
      runner.run(latex).result.getOrElse(latex)
    } catch {
      case _ => latex
    }

}
