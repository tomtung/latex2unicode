package com.github.tomtung.latex2unicode.translator

import org.parboiled.scala.Parser

object BinaryTranslator extends Parser {
  val names = Set("\\frac")

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

  def translate(command: String, param1: String, param2: String): String =
    if (!names.contains(command)) ""
    else {
      val p1 = param1.trim
      val p2 = param2.trim
      if (frac.contains((p1, p2)))
        frac((p1, p2))
      else
        "(" + p1 + "/" + p2 + ")"
    }
}
