package com.github.tomtung.latex2unicode

import org.parboiled.scala._

class LatexParser extends Parser {
  val whiteSpaces = " \r\n\t".toCharArray
  // #, %, & and ~ are treated as char literals. $ is used only as a separator.
  val nonSpacePunctuations = "$^_{}\\".toCharArray
  val punctuations = whiteSpaces ++ nonSpacePunctuations

  def matchCommandNames(names: Iterable[String], punc: Array[Char] = punctuations): Rule1[String] = {
    def self[T](a: T): T = a
    def getRule(name: String): Rule0 = name ~
      (if (!punc.contains(name.last)) &(anyOf(punc) | EOI) else EMPTY)

    names.foldLeft(NOTHING)((rule, s) => rule | getRule(s)) ~> self
  }

  def Input: Rule1[String] = rule {
    (Text ~ EOI) | (EOI ~ push(""))
  }

  def Text: Rule1[String] = rule {
    (Expression | WhiteSpaces) ~ optional(Text ~~> ((a: String, b) => a + b))
  }

  def Expression: Rule1[String] = rule {
    CharLiteral | Group | Command
  }

  def Group: Rule1[String] = rule {
    "{}" ~ push("") | "{" ~ Text ~ "}"
  }

  def CharLiteral: Rule1[String] = rule {
    noneOf(punctuations) ~> ((s: String) => s)
  }

  def WhiteSpaces: Rule1[String] = rule {
    val newline = sys.props("line.separator")
    Spaces ~ push(" ") | SpacesMultiNewLine ~ push(newline + newline)
  }

  def Spaces: Rule0 = rule {
    oneOrMore(anyOf(whiteSpaces)) ~? (_.count(_ == '\n') <= 1)
  }

  def SpacesMultiNewLine: Rule0 = rule {
    oneOrMore(anyOf(whiteSpaces)) ~? (_.count(_ == '\n') > 1)
  }

  def Command: Rule1[String] = rule {
    Escape | Unary | UnaryWithOption | Binary | Style | UnknownCommand
  }

  def Escape: Rule1[String] = rule {
    matchCommandNames(EscapeTranslator.names) ~~> EscapeTranslator.translate
  }

  def Unary: Rule1[String] = rule {
    matchCommandNames(UnaryTranslator.names) ~
      optional(Spaces) ~ Expression ~~> UnaryTranslator.translate
  }

  def UnaryWithOption: Rule1[String] = rule {
    matchCommandNames(UnaryWithOptionTranslator.names, punctuations :+ '[') ~ optional(Spaces) ~
      (CommandOption ~ optional(Spaces) | &(!str("[")) ~ EMPTY ~ push("")) ~
      Expression ~~> UnaryWithOptionTranslator.translate
  }

  def CommandOption: Rule1[String] = rule {
    "[]" ~ push("") | "[" ~ CommandOptionText ~ "]"
  }

  def CommandOptionText: Rule1[String] = rule {
    &(!str("]")) ~ (Expression | WhiteSpaces) ~ optional(CommandOptionText ~~> ((a: String, b) => a + b))
  }

  def Binary: Rule1[String] = rule {
    matchCommandNames(BinaryTranslator.names) ~ optional(Spaces) ~
      Expression ~ optional(Spaces) ~ Expression ~~> BinaryTranslator.translate
  }

  def Style: Rule1[String] = rule {
    matchCommandNames(StyleTranslator.names) ~ Text ~~> StyleTranslator.translate
  }

  def UnknownCommand: Rule1[String] = rule {
    "\\" ~ oneOrMore(noneOf(punctuations)) ~ push("")
  }

}
