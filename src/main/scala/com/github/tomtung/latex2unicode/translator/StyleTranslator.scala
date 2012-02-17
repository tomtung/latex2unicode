package com.github.tomtung.latex2unicode.translator

object StyleTranslator {
  lazy val names = alias.keySet

  val alias = Map(
    "\\bf" -> "\\textbf",
    "\\cal" -> "\\textcal",
    "\\it" -> "\\textit",
    "\\tt" -> "\\texttt"
  )

  def translate(command: String, text: String): String =
    if (!names.contains(command)) ""
    else UnaryTranslator.translate(alias(command), text)
}
