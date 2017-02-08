package com.github.tomtung.latex2unicode.helper

object Style {
  val alias = Map(
    "\\bf" -> "\\textbf",
    "\\cal" -> "\\textcal",
    "\\it" -> "\\textit",
    "\\tt" -> "\\texttt"
  )

  val names: Set[String] = alias.keySet

  def translate(command: String, text: String): String = {
    if (!names.contains(command)) {
      throw new IllegalArgumentException(s"Unknown command: $command")
    }

    Unary.translate(alias(command), text)
  }
}
