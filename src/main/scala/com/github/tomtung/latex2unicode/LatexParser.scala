package com.github.tomtung.latex2unicode

import org.parboiled.scala._

/**
 * A Parboiled parser that contains the rules to convert LaTeX markup to Unicode. <br/>
 * You can override command names fields (*Names) or translation methods (translate*) to change its behavior.
 * You can also use this class along with parboiled library to achive more flexibility in error report, recovery, etc.
 */
class LatexParser extends Parser {

  protected val whiteSpaces = " \r\n\t".toCharArray
  // #, % and & are treated as char literals. $ is used only as a separator.
  protected val nonSpacePunctuations = "$^_~{}\\".toCharArray
  protected val punctuations = whiteSpaces ++ nonSpacePunctuations

  protected def matchCommandNames(names: Iterable[String], punc: Array[Char] = punctuations): Rule1[String] = {
    def self[T](a: T): T = a
    def getRule(name: String): Rule0 = name ~
      (if (!punc.contains(name.last)) &(anyOf(punc) | EOI) else EMPTY)

    names.foldLeft(NOTHING)((rule, s) => rule | getRule(s)) ~> self
  }

  // ------ Data ------

  protected def escapeNames = helper.Escape.names

  protected def unaryNames = helper.Unary.names

  protected def unaryWithOptionNames = helper.UnaryWithOption.names

  protected def binaryNames = helper.Binary.names

  protected def styleNames = helper.Style.names

  // ------ Translation Method ------

  protected def translateCharLiteral(matched: String) = matched

  protected def translateSpaces(matched: String) = " "

  protected def translateSpacesMultiNewLine(matched: String) = {
    val newline = sys.props("line.separator")
    newline + newline
  }

  protected def translateEscape(name: String): String = helper.Escape.translate(name)

  protected def translateUnary(command: String, param: String) = helper.Unary.translate(command, param)

  protected def translateUnaryWithOption(command: String, option: String, param: String) = helper.UnaryWithOption.translate(command, option, param)

  protected def translateBinary(command: String, param1: String, param2: String) = helper.Binary.translate(command, param1, param2)

  protected def translateStyle(command: String, text: String) = helper.Style.translate(command, text)

  protected def translateUnknownCommand(matched: String) = ""

  // ------ PEG definition ------

  /**
   * The starting rule.
   */
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
    noneOf(punctuations) ~> translateCharLiteral
  }

  def WhiteSpaces: Rule1[String] = rule {
    Spaces ~> translateSpaces |
      SpacesMultiNewLine ~> translateSpacesMultiNewLine
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
    matchCommandNames(escapeNames) ~~> translateEscape
  }

  def Unary: Rule1[String] = rule {
    matchCommandNames(unaryNames) ~
      optional(Spaces) ~ Expression ~~> translateUnary
  }

  def UnaryWithOption: Rule1[String] = rule {
    matchCommandNames(unaryWithOptionNames, punctuations :+ '[') ~ optional(Spaces) ~
      (CommandOption ~ optional(Spaces) | &(!str("[")) ~ EMPTY ~ push("")) ~
      Expression ~~> translateUnaryWithOption
  }

  def CommandOption: Rule1[String] = rule {
    "[]" ~ push("") | "[" ~ CommandOptionText ~ "]"
  }

  def CommandOptionText: Rule1[String] = rule {
    &(!str("]")) ~ (Expression | WhiteSpaces) ~ optional(CommandOptionText ~~> ((a: String, b) => a + b))
  }

  def Binary: Rule1[String] = rule {
    matchCommandNames(binaryNames) ~ optional(Spaces) ~
      Expression ~ optional(Spaces) ~ Expression ~~> translateBinary
  }

  def Style: Rule1[String] = rule {
    matchCommandNames(styleNames) ~ Text ~~> translateStyle
  }

  def UnknownCommand: Rule1[String] = rule {
    "\\" ~ oneOrMore(noneOf(punctuations)) ~> translateUnknownCommand
  }
}
