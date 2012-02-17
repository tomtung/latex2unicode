package com.github.tomtung.latex2unicode.helper

object UnaryWithOption {
  val names = Set("\\sqrt")

  val sqrt = Map(
    "" -> "√",
    "2" -> "√",
    "3" -> "∛",
    "4" -> "∜"
  )

  def translate(command: String, option: String, param: String): String =
    if (!names.contains(command)) ""
    else {
      assert(command == "\\sqrt")
      sqrt.getOrElse(option.trim, Unary.translate("^", option.trim) + "√") +
        "(" + param.trim + ")"
    }
}
