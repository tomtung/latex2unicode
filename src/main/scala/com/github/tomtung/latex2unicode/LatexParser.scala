package com.github.tomtung.latex2unicode

import org.parboiled.scala._
import collection.Iterable

/**
 * A Parboiled parser that contains the rules to convert LaTeX markup to Unicode. <br/>
 * You can override command names fields (*Names) or translation methods (translate*) to change its behavior.
 * You can also use this class along with parboiled library to achieve more flexibility in error report, recovery, etc.
 */
class LatexParser extends Parser {

  private val whiteSpaces = " \r\n\t".toCharArray
  // #, % and & are treated as char literals. $ is used only as a separator.
  private val nonSpacePunctuations = "$^_~{}\\".toCharArray
  private val punctuations = whiteSpaces ++ nonSpacePunctuations

  private def matchCommandNames(names: Iterable[String]): Rule1[String] = {
    def self[T](a: T): T = a
    def getRule(name: String): Rule0 = name ~
      (if (name.last.isLetter) &(!("a" - "z" | "A" - "Z")) else EMPTY)

    names.foldLeft(NOTHING)((rule, s) => rule | getRule(s)) ~> self
  }

  // ------ Data ------

  /**
   * Names used to match Escape.
   */
  protected val escapeNames: Iterable[String] = helper.Escape.names

  /**
   * Names used to match Unary.
   */
  protected val unaryNames: Iterable[String] = helper.Unary.names

  /**
   * Names used to match UnaryWithOption.
   */
  protected val unaryWithOptionNames: Iterable[String] = helper.UnaryWithOption.names

  /**
   * Names used to match Binary.
   */
  protected val binaryNames: Iterable[String] = helper.Binary.names

  /**
   * Names used to match Style.
   */
  protected val styleNames: Iterable[String] = helper.Style.names

  // ------ Translation Method ------

  /**
   * Method for translating CharLiteral.
   */
  protected def translateCharLiteral(matched: String) = matched

  /**
   * Method for translating Spaces.
   */
  protected def translateSpaces(matched: String) = " "

  /**
   * Method for translating SpacesMultiNewLine.
   */
  protected def translateSpacesMultiNewLine(matched: String) = {
    val newline = sys.props("line.separator")
    newline + newline
  }

  /**
   * Method for translating Escape.
   */
  protected def translateEscape(name: String): String = helper.Escape.translate(name)

  /**
   * Method for translating Unary.
   */
  protected def translateUnary(command: String, param: String) = helper.Unary.translate(command, param)

  /**
   * Method for translating UnaryWithOption.
   */
  protected def translateUnaryWithOption(command: String, option: String, param: String) = helper.UnaryWithOption.translate(command, option, param)

  /**
   * Method for translating Binary.
   */
  protected def translateBinary(command: String, param1: String, param2: String) = helper.Binary.translate(command, param1, param2)

  /**
   * Method for translating Style.
   */
  protected def translateStyle(command: String, text: String) = helper.Style.translate(command, text)

  /**
   * Method for translating UnknownCommand.
   */
  protected def translateUnknownCommand(matched: String) = ""

  // ------ PEG definition ------

  /**
   * The starting rule, matches Text and empty string.
   */
  def Input: Rule1[String] = rule {
    (Text ~ EOI) | (EOI ~ push(""))
  }

  /**
   * Text ← (Expression / WhiteSpaces) Text?
   */
  def Text: Rule1[String] = rule {
    (Expression | WhiteSpaces) ~ optional(Text ~~> ((a: String, b) => a + b))
  }

  /**
   * Expression ← CharLiteral / Group / Command
   */
  def Expression: Rule1[String] = rule {
    CharLiteral | Group | Command
  }

  /**
   * Group ← '{}' / '{'  Text '}'
   */
  def Group: Rule1[String] = rule {
    "{}" ~ push("") | "{" ~ Text ~ "}"
  }

  /**
   * Matches any character that does not need to be escaped.
   */
  def CharLiteral: Rule1[String] = rule {
    noneOf(punctuations) ~> translateCharLiteral
  }

  /**
   * WhiteSpaces ← Spaces / SpacesMultiNewLine
   */
  def WhiteSpaces: Rule1[String] = rule {
    Spaces ~> translateSpaces |
      SpacesMultiNewLine ~> translateSpacesMultiNewLine
  }

  /**
   * A sequence of white space characters which contains at most one "new line".
   */
  def Spaces: Rule0 = rule {
    oneOrMore(anyOf(whiteSpaces)) ~? (_.count(_ == '\n') <= 1)
  }

  /**
   * A sequence of white space characters which contains more than one "new line".
   */
  def SpacesMultiNewLine: Rule0 = rule {
    oneOrMore(anyOf(whiteSpaces)) ~? (_.count(_ == '\n') > 1)
  }

  /**
   * Command ← Escape / Unary / UnaryWithOption / Binary / Style / UnknownCommand
   */
  def Command: Rule1[String] = rule {
    Escape | Unary | UnaryWithOption | Binary | Style | UnknownCommand
  }

  /**
   * Matches according to the escapeNames field.
   */
  def Escape: Rule1[String] = rule {
    matchCommandNames(escapeNames) ~~> translateEscape
  }

  /**
   * Unary ← UnaryName Spaces? Expression <br/>
   * UnaryName matches according to the unaryNames field.
   */
  def Unary: Rule1[String] = rule {
    matchCommandNames(unaryNames) ~
      optional(Spaces) ~ Expression ~~> translateUnary
  }

  /**
   * UnaryWithOption ← UnaryWithOptionName Spaces? '[' CommandOption Spaces? ']' Expression <br/>
   * UnaryWithOptionName matches according to the unaryWithOptionNames field.
   */
  def UnaryWithOption: Rule1[String] = rule {
    matchCommandNames(unaryWithOptionNames) ~ optional(Spaces) ~
      (CommandOption ~ optional(Spaces) | &(!str("[")) ~ EMPTY ~ push("")) ~
      Expression ~~> translateUnaryWithOption
  }

  /**
   * CommandOption ← '[]' / '[' CommandText ']'
   */
  def CommandOption: Rule1[String] = rule {
    "[]" ~ push("") | "[" ~ CommandOptionText ~ "]"
  }

  /**
   * Similar to Text, but cannot start with ']'
   */
  def CommandOptionText: Rule1[String] = rule {
    &(!str("]")) ~ (Expression | WhiteSpaces) ~ optional(CommandOptionText ~~> ((a: String, b) => a + b))
  }

  /**
   * Biary ← BinaryName Spaces? Expression Spaces? Expression <br/>
   * BinaryName matches according to the binaryNames field.
   */
  def Binary: Rule1[String] = rule {
    matchCommandNames(binaryNames) ~ optional(Spaces) ~
      Expression ~ optional(Spaces) ~ Expression ~~> translateBinary
  }

  /**
   * Style ← StyleName Text <br/>
   * StyleName matches according to the styleNames field.
   */
  def Style: Rule1[String] = rule {
    matchCommandNames(styleNames) ~ Text ~~> translateStyle
  }

  /**
   * UnknownCommand ← '\\' Letter+ (Spaces? CommandOption)? Spaces?
   */
  def UnknownCommand: Rule1[String] = rule {
    "\\" ~ oneOrMore("a" - "z" | "A" - "Z") ~
      optional(optional(Spaces) ~ CommandOption ~~% (o => {})) ~
      optional(Spaces) ~> translateUnknownCommand
  }
}

/**
 * A LatexParser singleton.
 */
object LatexParser extends LatexParser