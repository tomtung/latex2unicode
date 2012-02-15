package com.github.tomtung.latex2unicode

import org.parboiled.scala._
import org.parboiled.support.ParseTreeUtils

object Main {
  def main(args: Array[String]) {
    println("Enter LaTex markup to translate to Unicode. Enter \"quit\" to quit the program.")

    val parser = new LatexParser {
      override val buildParseTree = true
    }
    val runner = RecoveringParseRunner(parser.Input)

    while (true) {
      val input = readLine()
      if (input == "quit") sys.exit()

      try {
        val result = runner.run(input)
        result.parseErrors.foreach(e => println(e + " (" + e.getStartIndex + "," + e.getEndIndex + ")"))

        val parseTreePrintOut = ParseTreeUtils.printNodeTree(result)
        println(parseTreePrintOut)
        println(result.result.getOrElse("<Unrecoverable error>"))
      } catch {
        case e: Throwable =>
          println("<Unrecoverable error>")
          e.printStackTrace()
      }
    }
  }
}
