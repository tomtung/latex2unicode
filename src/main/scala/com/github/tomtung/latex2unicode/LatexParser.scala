package com.github.tomtung.latex2unicode

import org.parboiled.scala._
import translator._

/**
 * This is a Parboiled parser that contains the rules to convert LaTeX markup to Unicode. <br/>
 * This class can be used along with parboiled library to provide more flexibility in error report, recovery, etc.
 * You can also override command names fields (*Names) and translation methods (translate*) to change its behavior.
 * For simple conversion, use {@link com.github.tomtung.latex2unicode.LatexToUnicodeConverter#convert} in stead.
 */
class LatexParser extends Parser {
  private val whiteSpaces = " \r\n\t".toCharArray
  // #, %, & and ~ are treated as char literals. $ is used only as a separator.
  private val nonSpacePunctuations = "$^_{}\\".toCharArray
  private val punctuations = whiteSpaces ++ nonSpacePunctuations

  private def matchCommandNames(names: Iterable[String], punc: Array[Char] = punctuations): Rule1[String] = {
    def self[T](a: T): T = a
    def getRule(name: String): Rule0 = name ~
      (if (!punc.contains(name.last)) &(anyOf(punc) | EOI) else EMPTY)

    names.foldLeft(NOTHING)((rule, s) => rule | getRule(s)) ~> self
  }
  
  // ------ Data ------
  
  val escapeNames = EscapeTranslator.names
  
  val unaryNames = UnaryTranslator.names
  
  val unaryWithOptionNames = UnaryWithOptionTranslator.names

  val binaryNames = BinaryTranslator.names

  val styleNames = StyleTranslator.names
  
  // ------ Translation Method ------

  def translateCharLiteral(matched: String) = matched

  def translateSpaces(matched: String) = " "

  def translateSpacesMultiNewLine(matched: String) = {
    val newline = sys.props("line.separator")
    newline + newline
  }

  def translateEscape(name: String): String = EscapeTranslator.translate(name)
  
  def translateUnary(command: String, param: String) = UnaryTranslator.translate(command, param)
  
  def translateUnaryWithOption(command: String, option: String, param: String) = UnaryWithOptionTranslator.translate(command, option, param)
  
  def translateBinary(command: String, param1: String, param2: String) = BinaryTranslator.translate(command, param1, param2)
  
  def translateStyle(command: String, text: String) = StyleTranslator.translate(command, text)
  
  def translateUnknownCommand(matched: String) = ""
  
  // ------ PEG definition ------

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
    matchCommandNames(StyleTranslator.names) ~ Text ~~> translateStyle
  }

  def UnknownCommand: Rule1[String] = rule {
    "\\" ~ oneOrMore(noneOf(punctuations)) ~> translateUnknownCommand
  }
  
}
