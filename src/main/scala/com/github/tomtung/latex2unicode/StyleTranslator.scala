package com.github.tomtung.latex2unicode

object StyleTranslator {
  lazy val names = alias.keys

  val alias = Map(
    "\\bf" -> "\\mathbf",
    "\\cal" -> "\\mathcal",
    "\\it" -> "\\mathit",
    "\\tt" -> "\\mathtt"
  )

  def translate(command: String, text: String): String = {
    UnaryTranslator.translate(alias(command), text)
  }
}
