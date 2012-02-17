package com.github.tomtung.latex2unicode.helper

object Style {
  lazy val names = alias.keySet

  val alias = Map(
    "\\bf" -> "\\textbf",
    "\\cal" -> "\\textcal",
    "\\it" -> "\\textit",
    "\\tt" -> "\\texttt"
  )

  def translate(command: String, text: String): String =
    if (!names.contains(command)) ""
    else Unary.translate(alias(command), text)
}
