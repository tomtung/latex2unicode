package com.github.tomtung.latex2unicode

import fastparse.all.Parsed.Failure
import org.scalatest._
import org.scalatest.Matchers._

class LaTeX2UnicodeTest extends FunSuite {

  test("Empty string") {
    LaTeX2Unicode.convert("") shouldBe ""
  }

  test("Literal") {
    LaTeX2Unicode.convert("This, is a test.") shouldBe "This, is a test."
  }

  test("Spaces") {
    LaTeX2Unicode.convert("\nthis  \t \tis    \n\t\n \n \t\t\na  \n\t  test   ") shouldBe " this is\n\na test "
  }

  test("Brackets") {
    LaTeX2Unicode.convert("{this {{i}{s}}{ a \n\n} test}") shouldBe "this is a\n\n test"

    LaTeX2Unicode.parse("{") shouldBe a[Failure]
    LaTeX2Unicode.parse("{ ") shouldBe a[Failure]
    LaTeX2Unicode.parse("{ \n\n") shouldBe a[Failure]
  }

  test("Escape") {
    LaTeX2Unicode.convert("""\S\{this is ~$\alpha$~ test\}""") shouldBe "§{this is  α  test}"
  }

  test("Subscript") {
    LaTeX2Unicode.convert("i_{}") shouldBe "i"
    LaTeX2Unicode.convert("i_123") shouldBe "i₁23"
    LaTeX2Unicode.convert("i_\n  {123}") shouldBe "i₁₂₃"
    LaTeX2Unicode.convert("i_\n  { 123 }") shouldBe "i₁₂₃"
    LaTeX2Unicode.convert("i_{i_{123 }}") shouldBe "i_(i₁₂₃)"
    LaTeX2Unicode.convert("i_{i_{1~2~3 }}") shouldBe "i_(i₁ ₂ ₃)"
    LaTeX2Unicode.convert("i\\textsubscript 123") shouldBe "i₁23"
    LaTeX2Unicode.convert("i\\textsubscript{123}") shouldBe "i₁₂₃"
    LaTeX2Unicode.convert("i\\textsubscript\n  { 123 }") shouldBe "i₁₂₃"
    LaTeX2Unicode.convert("i\\textsubscript{i\\textsubscript{123 }}") shouldBe "i_(i₁₂₃)"

    LaTeX2Unicode.parse("_") shouldBe a[Failure]
    LaTeX2Unicode.parse("_ ") shouldBe a[Failure]
    LaTeX2Unicode.parse("_ \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("_ \n\nx") shouldBe a[Failure]
  }

  test("Superscript") {
    LaTeX2Unicode.convert("i^{}") shouldBe "i"
    LaTeX2Unicode.convert("i^123") shouldBe "i¹23"
    LaTeX2Unicode.convert("i^{123}") shouldBe "i¹²³"
    LaTeX2Unicode.convert("i^\n  { 123 }") shouldBe "i¹²³"
    LaTeX2Unicode.convert("i^{i^{123 }}") shouldBe "i^(i¹²³)"
    LaTeX2Unicode.convert("i^{i^{1~2~3 }}") shouldBe "i^(i¹ ² ³)"
    LaTeX2Unicode.convert("i\\textsuperscript 123") shouldBe "i¹23"
    LaTeX2Unicode.convert("i\\textsuperscript{123}") shouldBe "i¹²³"
    LaTeX2Unicode.convert("i\\textsuperscript\n  { 123 }") shouldBe "i¹²³"
    LaTeX2Unicode.convert("i\\textsuperscript{i\\textsuperscript{123 }}") shouldBe "i^(i¹²³)"

    LaTeX2Unicode.parse("^") shouldBe a[Failure]
    LaTeX2Unicode.parse("^ ") shouldBe a[Failure]
    LaTeX2Unicode.parse("^ \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("^ \n\nx") shouldBe a[Failure]
  }

  test("\\not") {
    LaTeX2Unicode.convert("""\not 1""") shouldBe "1̸"
    LaTeX2Unicode.convert("""\not{123}""") shouldBe "1̸23"
    LaTeX2Unicode.convert("""\not{ 123 }""") shouldBe "1̸23"
    LaTeX2Unicode.convert("""\not=""") shouldBe "≠"
    LaTeX2Unicode.convert("""\not \in""") shouldBe "∉"
    LaTeX2Unicode.convert("""\not{}""") shouldBe " ̸"

    LaTeX2Unicode.parse("\\not") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\not ") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\not \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\not \n\nx") shouldBe a[Failure]
  }

  test("Combining") {
    LaTeX2Unicode.convert("\\bar ab") shouldBe "a\u0304b"
    LaTeX2Unicode.convert("\\bar12") shouldBe "1\u03042"
    LaTeX2Unicode.convert("\\bar{}") shouldBe " \u0304"
    LaTeX2Unicode.convert("\\=ab") shouldBe "a\u0304b"
    LaTeX2Unicode.convert("\\=\nab") shouldBe "a\u0304b"
    LaTeX2Unicode.convert("\\={}") shouldBe " \u0304"
    LaTeX2Unicode.convert("\\bar{ab}") shouldBe "a\u0304b"
    LaTeX2Unicode.convert("\\={ab}") shouldBe "a\u0304b"
    LaTeX2Unicode.convert("\\=\\k\\underline\\overline{a\\=bc}") shouldBe "a\u0305\u0332\u0304b\u0304\u0305\u0332c\u0305\u0332\u0328"

    LaTeX2Unicode.parse("\\bar") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\bar ") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\bar \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\=") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\= ") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\= \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\= \n\nx") shouldBe a[Failure]
  }

  test("Style") {
    LaTeX2Unicode.convert("\\mathbf{}") shouldBe ""
    LaTeX2Unicode.convert("\\mathbf ABC \\mathit ABC") shouldBe "𝐀BC 𝐴BC"
    LaTeX2Unicode.convert("\\mathbf {ABC} \\mathit {ABC}") shouldBe "𝐀𝐁𝐂 𝐴𝐵𝐶"
    LaTeX2Unicode.convert("\\bf \\it ") shouldBe ""
    LaTeX2Unicode.convert("ABC {\\bf ABC} {\\it ABC} ABC") shouldBe "ABC 𝐀𝐁𝐂 𝐴𝐵𝐶 ABC"
    LaTeX2Unicode.convert("ABC \\bf ABC \\it ABC ABC") shouldBe "ABC 𝐀𝐁𝐂 𝐴𝐵𝐶 𝐴𝐵𝐶"
    LaTeX2Unicode.convert("A\\bf\n\nB\n\nC") shouldBe "A\n\n𝐁\n\n𝐂"

    LaTeX2Unicode.parse("\\mathbf") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\mathbf ") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\mathbf \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\mathbf \n\nx") shouldBe a[Failure]
  }

  test("\\sqrt") {
    LaTeX2Unicode.convert("\\sqrt{}") shouldBe "√"
    LaTeX2Unicode.convert("\\sqrt x") shouldBe "√x̅"
    LaTeX2Unicode.convert("\\sqrt\nx") shouldBe "√x̅"
    LaTeX2Unicode.convert("\\sqrt[]x") shouldBe "√x̅"
    LaTeX2Unicode.convert("\\sqrt[]\nx") shouldBe "√x̅"
    LaTeX2Unicode.convert("\\sqrt{x+1}") shouldBe "√x̅+̅1̅"
    LaTeX2Unicode.convert("\\sqrt2") shouldBe "√2̅"
    LaTeX2Unicode.convert("\\sqrt1+1") shouldBe "√1̅+1"
    LaTeX2Unicode.convert("\\sqrt[2]x") shouldBe "√x̅"
    LaTeX2Unicode.convert("\\sqrt[3]{x}") shouldBe "∛x̅"
    LaTeX2Unicode.convert("\\sqrt[3]{}") shouldBe "∛"
    LaTeX2Unicode.convert("\\sqrt[\\alpha+1]x") shouldBe "ᵅ⁺¹√x̅"
    LaTeX2Unicode.convert("\\sqrt[\\alpha+1]\nx") shouldBe "ᵅ⁺¹√x̅"
    LaTeX2Unicode.convert("\\sqrt[q]{x}") shouldBe "(q)√x̅"

    LaTeX2Unicode.parse("\\sqrt") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\sqrt  ") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\sqrt  \n\n{}") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\sqrt[]") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\sqrt\n\n[]{}") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\sqrt[]\n\n{}") shouldBe a[Failure]
  }

  test("\\frac") {
    LaTeX2Unicode.convert("\\frac{}{}") shouldBe ""
    LaTeX2Unicode.convert("\\frac34") shouldBe "¾"
    LaTeX2Unicode.convert("\\frac 34") shouldBe "¾"
    LaTeX2Unicode.convert("\\frac\n34") shouldBe "¾"
    LaTeX2Unicode.convert("\\frac{\\hat\\alpha_1^2}{test\\_test}") shouldBe "(α̂₁²/test_test)"
    LaTeX2Unicode.convert("\\frac{1}{}") shouldBe "(1/)"
    LaTeX2Unicode.convert("\\frac{}{1}") shouldBe "(/1)"
    LaTeX2Unicode.convert("\\frac{a+b}{c}") shouldBe "((a+b)/c)"
    LaTeX2Unicode.convert("\\frac{a}{b+c}") shouldBe "(a/(b+c))"
    LaTeX2Unicode.convert("\\frac{a+b}{c+d}") shouldBe "((a+b)/(c+d))"

    LaTeX2Unicode.parse("\\frac") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\frac  ") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\frac  \n\n") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\frac{}") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\frac{}  \n\n{}") shouldBe a[Failure]
    LaTeX2Unicode.parse("\\frac\n\n{}{}") shouldBe a[Failure]
  }

  test("Unknown commands") {
    val str = "\\this \\is \\a \\test"
    LaTeX2Unicode.convert(str) shouldBe str
  }

}
