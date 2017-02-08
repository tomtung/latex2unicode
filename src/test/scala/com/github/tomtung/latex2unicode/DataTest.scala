package com.github.tomtung.latex2unicode

import com.github.tomtung.latex2unicode.helper._
import org.scalatest._
import org.scalatest.Matchers._

class DataTest extends FunSuite {

  test("Command names should be trimmed") {
    for (
      names <- List(Escape.names, Unary.names, Style.names, UnaryWithOption.names, Binary.names);
      name <- names
    ) {
      name.trim should equal(name)
    }
  }

}
