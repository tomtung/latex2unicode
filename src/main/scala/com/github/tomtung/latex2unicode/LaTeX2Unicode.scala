package com.github.tomtung.latex2unicode

object LaTeX2Unicode {

  import fastparse.all._

  private def isLiteralChar(c: Char): Boolean = !c.isWhitespace && "$^_~{}\\".indexOf(c) == -1

  private val spacesCountNewLines: Parser[Int] = P(CharsWhile(_.isWhitespace).! ~/ Pass).map(_.count(_ == '\n'))

  private val spacesBlock: Parser[String] = spacesCountNewLines.map(cnt => {
    if (cnt <= 1) " "
    else "\n\n"
  })

  private val literalCharsBlock: Parser[String] = P(CharsWhile(isLiteralChar) ~/ Pass).!

  private val bracketBlock: Parser[String] = P("{" ~/ blocks ~ "}" ~/ Pass)

  private object command {
    private val ignoreSpaces: Parser[Unit] = P(spacesCountNewLines.?).flatMap({
      case None => Pass
      case Some(cnt) =>
        if (cnt <= 1) Pass
        else Fail
    })

    private val PassWithEmptyString: Parser[String] = PassWith("")

    private val PassWithNewLine: Parser[String] = PassWith("\n\n")

    private val maybeNewLine: Parser[String] = P(spacesCountNewLines.?).flatMap({
      case None => PassWithEmptyString
      case Some(cnt) =>
        if (cnt <= 1) PassWithEmptyString
        else PassWithNewLine
    })

    private val param: Parser[String] = P(bracketBlock | command.block | P(CharPred(isLiteralChar).!))

    private val name: Parser[String] = P(
      (CharIn("$^_~").! ~/ Pass) |
        ("\\" ~/ (CharsWhile(_.isLetter) | AnyChar) ~/ Pass).!
    )

    // Literals inside option must not contain "]"
    private val literalCharsBlockInOption: Parser[String] = P(CharsWhile(c => c != ']' && isLiteralChar(c))).!

    private val commandBlockInOption = P(name.flatMap(s => {
      // Ignoring styles in command option is just for simplicity
      if (helper.Style.names.contains(s) || !nameToParser.contains(s)) PassWith("")
      else nameToParser(s)
    }))
    private val blockInOption: Parser[String] = P(literalCharsBlockInOption | bracketBlock | commandBlockInOption)

    private val blocksInOption: Parser[String] = P(blockInOption.rep).map(_.mkString)

    private val nameToParser: Map[String, Parser[String]] = {
      val builder = Map.newBuilder[String, Parser[String]]

      for (s <- helper.Escape.names) {
        val parser = Pass.map(_ => helper.Escape.translate(s))
        builder += s -> parser
      }

      for (s <- helper.Unary.names) {
        val parser = P(ignoreSpaces ~ param).map(p => helper.Unary.translate(s, p))
        builder += s -> parser
      }

      for (s <- helper.Binary.names) {
        val parser = P(ignoreSpaces ~ param ~ ignoreSpaces ~ param).map({
          case (p1, p2) => helper.Binary.translate(s, p1, p2)
        })
        builder += s -> parser
      }

      for (s <- helper.Style.names) {
        val parser = P(maybeNewLine ~ blocks).map({
          case (nl, p) => nl + helper.Style.translate(s, p)
        })
        builder += s -> parser
      }

      for (s <- helper.UnaryWithOption.names) {
        val parser = P(
          ignoreSpaces ~ ("[" ~/ ignoreSpaces ~ blocksInOption ~ ignoreSpaces ~ "]").? ~/
            ignoreSpaces ~ param
        ).map({
            case (opt, p) => helper.UnaryWithOption.translate(s, opt.getOrElse(""), p)
          })
        builder += s -> parser
      }

      builder.result()
    }

    val block: Parser[String] = {
      val knownCommandBlock = name.filter(nameToParser.contains).flatMap(nameToParser)
      val unknownCommand = name
      P(knownCommandBlock | unknownCommand)
    }
  }

  private val block: Parser[String] = P(spacesBlock | literalCharsBlock | bracketBlock | command.block)

  private val blocks: Parser[String] = P(block.rep).map(_.mkString)

  private val input: Parser[String] = P(blocks ~ End)

  /**
   * Parse and try to convert LaTeX markup to Unicode.
   * @param latex LaTeX markup
   * @return a fastparse Parsed object that contains parsing result information.
   */
  def parse(latex: String): Parsed[String] = input.parse(latex)

  /**
   * Converts LaTeX markup to Unicode whenever possible. <br />
   * When parse fails, simply fallback to the original input string.
   * @param latex LaTeX markup
   * @return Resultant Unicode string
   */
  def convert(latex: String): String = try {
    parse(latex) match {
      case Parsed.Success(result, _) =>
        result

      // If parsing fails, just return the original string
      case Parsed.Failure(_, _, _) =>
        latex
    }
  } catch {
    // If anything bad happens, just return the original string
    case e: Throwable => latex
  }
}