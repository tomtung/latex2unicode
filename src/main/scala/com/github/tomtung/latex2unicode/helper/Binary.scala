package com.github.tomtung.latex2unicode.helper

object Binary {
  // \frac command

  val frac = Map(
    ("1", "2") -> "½",
    ("1", "3") -> "⅓",
    ("2", "3") -> "⅔",
    ("1", "4") -> "¼",
    ("3", "4") -> "¾",
    ("1", "5") -> "⅕",
    ("2", "5") -> "⅖",
    ("3", "5") -> "⅗",
    ("4", "5") -> "⅘",
    ("1", "6") -> "⅙",
    ("5", "6") -> "⅚",
    ("1", "8") -> "⅛",
    ("3", "8") -> "⅜",
    ("5", "8") -> "⅝",
    ("7", "8") -> "⅞"
  )

  def shouldParenthesizeStringWithChar(c: Char): Boolean = {
    !c.isLetterOrDigit && !Unary.isCombiningChar(c) && {
      val charType = c.getType
      charType != Character.OTHER_NUMBER && charType != Character.CONNECTOR_PUNCTUATION
    }
  }

  def maybeParenthesize(s: String): String = {
    if (!s.exists(shouldParenthesizeStringWithChar)) s
    else s"($s)"
  }

  def makeFraction(numerator: String, denominator: String): String = {
    val (n, d) = (numerator.trim, denominator.trim)
    if (n.isEmpty && d.isEmpty) ""
    else
      frac.get((numerator.trim, denominator.trim)) match {
        case Some(s) =>
          s
        case None =>
          s"(${maybeParenthesize(numerator)}/${maybeParenthesize(denominator)})"
      }
  }

  // Common helper interface

  val names = Set("\\frac")

  def translate(command: String, param1: String, param2: String): String = {
    if (!names.contains(command)) {
      throw new IllegalArgumentException(s"Unknown command: $command")
    }

    assert(command == "\\frac")
    makeFraction(param1.trim, param2.trim)
  }
}
