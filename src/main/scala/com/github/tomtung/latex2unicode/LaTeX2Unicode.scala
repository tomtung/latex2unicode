package com.github.tomtung.latex2unicode

object LaTeX2Unicode {
  import fastparse._
  import fastparse.NoWhitespace._

  private[this] def isLiteralChar(c: Char): Boolean =
    !c.isWhitespace && "$^-_~{}\\".indexOf(c) == -1

  private[this] def spacesCountNewLines[_: P]: P[Int] =
    P(CharsWhile(_.isWhitespace).! ~/ Pass).map(_.count(_ == '\n'))

  /** space block must contains at least one space.
    */
  private[this] def spacesBlock[_: P]: P[String] =
    spacesCountNewLines.map(cnt => {
      if (cnt <= 1) " "
      else "\n\n"
    })

  private[this] def literalCharsBlock[_: P]: P[String] = P(
    CharsWhile(isLiteralChar) ~/ Pass
  ).!

  private[this] def bracketBlock[_: P]: P[String] = P(
    "{" ~/ blocks ~ "}" ~/ Pass
  )

  private[this] object command {
    private def ignoreSpaces[_: P]: P[Unit] = P(spacesCountNewLines.?).flatMap({
      case None => Pass
      case Some(cnt) =>
        if (cnt <= 1) Pass
        else Fail
    })

    private def PassWithEmptyString[_: P]: P[String] = Pass("")

    private def PassWithNewLine[_: P]: P[String] = Pass("\n\n")

    private def maybeNewLine[_: P]: P[String] =
      P(spacesCountNewLines.?).flatMap({
        case None => PassWithEmptyString
        case Some(cnt) =>
          if (cnt <= 1) PassWithEmptyString
          else PassWithNewLine
      })

    private def param[_: P]: P[String] = P(
      bracketBlock | command.commandBlock | P(CharPred(isLiteralChar).!)
    )

    def name[_: P]: P[String] = P(
      (("-".rep(1) | CharIn("$^_~")).! ~/ Pass) |
        ("\\" ~/ (CharsWhile(_.isLetter) | AnyChar) ~/ Pass).!
    )

    // Literals inside option must not contain "]"
    private def literalCharsBlockInOption[_: P]: P[String] = P(
      CharsWhile(c => c != ']' && isLiteralChar(c))
    ).!

    private def commandBlockInOption[_: P] = P(name.flatMap(s => {
      // Ignoring styles in command option is just for simplicity
      if (helper.Style.names.contains(s)) PassWithEmptyString
      else handleCommand.apply(s)
    }))
    private def blockInOption[_: P]: P[String] = P(
      literalCharsBlockInOption | bracketBlock | commandBlockInOption
    )

    private def blocksInOption[_: P]: P[String] =
      P(blockInOption.rep).map(_.mkString)

    def handleEscapeChars[_: P]: PartialFunction[String, P[String]] = {
      case e if helper.Escape.names.contains(e) =>
        Pass.map(_ => helper.Escape.translate(e))
    }
    def handleUnaries[_: P]: PartialFunction[String, P[String]] = {
      case u if helper.Unary.names.contains(u) =>
        P(ignoreSpaces ~ param).map(p => helper.Unary.translate(u, p))
    }
    def handleBinaries[_: P]: PartialFunction[String, P[String]] = {
      case b if helper.Binary.names.contains(b) =>
        P(ignoreSpaces ~ param ~ ignoreSpaces ~ param).map({ case (p1, p2) =>
          helper.Binary.translate(b, p1, p2)
        })
    }

    def handleStyles[_: P]: PartialFunction[String, P[String]] = {
      case s if helper.Style.names.contains(s) =>
        P(maybeNewLine ~ blocks).map({ case (nl, p) =>
          nl + helper.Style.translate(s, p)
        })
    }

    def handleUnaryWithOption[_: P]: PartialFunction[String, P[String]] = {
      case uo if helper.UnaryWithOption.names.contains(uo) =>
        P(
          ignoreSpaces ~ ("[" ~/ ignoreSpaces ~ blocksInOption ~ ignoreSpaces ~ "]").? ~/
            ignoreSpaces ~ param
        ).map({ case (opt, p) =>
          helper.UnaryWithOption.translate(uo, opt.getOrElse(""), p)
        })
    }
    def handleUnknown[_: P]: PartialFunction[String, P[String]] = {
      case other => unknownCommand(other)
    }

    def handleCommand[_: P]: PartialFunction[String, P[String]] =
      handleEscapeChars
        .orElse(handleUnaries)
        .orElse(handleBinaries)
        .orElse(handleStyles)
        .orElse(handleUnaryWithOption)
        .orElse(handleUnknown)

    def commandBlock[_: P]: P[String] = name.flatMap(handleCommand)

    def unknownCommand[_: P](command: String): P[String] = {
      if (!command.startsWith("\\")) {
        // Is not a command in the strong sense, so just return
        return Pass(command) // PassWith(command)
      }

      val parserNoParam = () => Pass(command)
      val parserUnary = () => P(param).map(p => command + p)
      val parserBinary = () =>
        P(param ~ param).map({ case (p1, p2) =>
          s"$command{$p1}{$p2}"
        })
      val parserTernary = () =>
        P(param ~ param ~ param).map({ case (p1, p2, p3) =>
          s"$command{$p1}{$p2}{$p3}"
        })

      P(parserTernary() | parserBinary() | parserUnary() | parserNoParam())
    }
  }

  private[this] def block[_: P]: P[String] = P(
    spacesBlock | literalCharsBlock | bracketBlock | command.commandBlock
  )

  private[this] def blocks[_: P]: P[String] = P(block.rep).map(_.mkString)

  private def input[_: P]: P[String] = P(blocks ~ End)

  /** Parse and try to convert LaTeX markup to Unicode.
    * @param latex
    *   LaTeX markup
    * @return
    *   a fastparse Parsed object that contains parsing result information.
    */
  def parse(latex: String): Parsed[String] =
    fastparse.parse[String](latex, input(_))

  def parseBlock(latex: String): Parsed[String] =
    fastparse.parse[String](latex, block(_))

  def parseBlocks(latex: String): Parsed[String] =
    fastparse.parse[String](latex, blocks(_))

  /** Converts LaTeX markup to Unicode whenever possible. <br /> When parse
    * fails, simply fallback to the original input string.
    * @param latex
    *   LaTeX markup
    * @return
    *   Resultant Unicode string
    */
  def convert(latex: String): String = try {
    this.parse(latex) match {
      case Parsed.Success(result, _) =>
        result

      // If parsing fails, just return the original string
      case Parsed.Failure(l, _, _) =>
        latex
    }
  } catch {
    // If anything bad happens, just return the original string
    case e: Throwable => latex
  }
}
