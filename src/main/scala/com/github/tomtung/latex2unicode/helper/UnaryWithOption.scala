package com.github.tomtung.latex2unicode.helper

object UnaryWithOption {
  def makeSqrt(index: String, radicand: String): String = {
    val radix = index match {
      case "" | "2" => "√"
      case "3"      => "∛"
      case "4"      => "∜"
      case _ => Unary.tryMakeSuperScript(index).getOrElse(s"($index)") + "√"
    }

    radix + Unary.translateCombining("\\overline", radicand)
  }

  val names = Set("\\sqrt")

  def translate(command: String, option: String, param: String): String = {
    if (!names.contains(command)) {
      throw new IllegalArgumentException(s"Unknown command: $command")
    }

    assert(command == "\\sqrt")
    makeSqrt(option.trim, param.trim)
  }
}
