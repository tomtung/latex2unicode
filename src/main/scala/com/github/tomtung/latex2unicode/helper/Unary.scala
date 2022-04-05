package com.github.tomtung.latex2unicode.helper

object Unary {

  // Commands that adds a combining character

  object CombiningType extends Enumeration {
    type CombiningType = Value
    val FirstChar, LastChar, EveryChar = Value
  }

  val combining: Map[String, (Char, CombiningType.Value)] = Map(
    "\\grave" -> ('\u0300', CombiningType.FirstChar),
    "\\`" -> ('\u0300', CombiningType.FirstChar),
    "\\acute" -> ('\u0301', CombiningType.FirstChar),
    "\\'" -> ('\u0301', CombiningType.FirstChar),
    "\\hat" -> ('\u0302', CombiningType.FirstChar),
    "\\^" -> ('\u0302', CombiningType.FirstChar),
    "\\tilde" -> ('\u0303', CombiningType.FirstChar),
    "\\~" -> ('\u0303', CombiningType.FirstChar),
    "\\bar" -> ('\u0304', CombiningType.FirstChar),
    "\\=" -> ('\u0304', CombiningType.FirstChar),
    "\\overline" -> ('\u0305', CombiningType.EveryChar),
    "\\breve" -> ('\u0306', CombiningType.FirstChar),
    "\\u" -> ('\u0306', CombiningType.FirstChar),
    "\\dot" -> ('\u0307', CombiningType.FirstChar),
    "\\." -> ('\u0307', CombiningType.FirstChar),
    "\\ddot" -> ('\u0308', CombiningType.FirstChar),
    "\\\"" -> ('\u0308', CombiningType.FirstChar),
    "\\mathring" -> ('\u030A', CombiningType.FirstChar),
    "\\r" -> ('\u030A', CombiningType.FirstChar),
    "\\H" -> ('\u030B', CombiningType.FirstChar),
    "\\check" -> ('\u030C', CombiningType.FirstChar),
    "\\v" -> ('\u030C', CombiningType.FirstChar),
    "\\d" -> ('\u0323', CombiningType.FirstChar),
    "\\c" -> ('\u0327', CombiningType.FirstChar),
    "\\k" -> ('\u0328', CombiningType.LastChar),
    "\\b" -> ('\u0332', CombiningType.FirstChar),
    "\\underline" -> ('\u0332', CombiningType.EveryChar),
    "\\underbar" -> ('\u0332', CombiningType.EveryChar),
    "\\t" -> ('\u0361', CombiningType.FirstChar),
    "\\vec" -> ('\u20D7', CombiningType.FirstChar),
    "\\textcircled" -> ('\u20DD', CombiningType.FirstChar)
  )

  def isCombiningChar(char: Char): Boolean = {
    '\u0300' <= char && char <= '\u036F' ||
    '\u1AB0' <= char && char <= '\u1AFF' ||
    '\u1DC0' <= char && char <= '\u1DFF' ||
    '\u20D0' <= char && char <= '\u20FF' ||
    '\uFE20' <= char && char <= '\uFE20'
  }

  def isCombiningOrControlChar(char: Char): Boolean = {
    Character.isISOControl(char) || isCombiningChar(char)
  }

  def isCombiningCommand(command: String): Boolean = combining.contains(command)

  def translateCombining(command: String, str: String): String = {
    if (!isCombiningCommand(command)) {
      throw new RuntimeException(s"Unknown combining command: $command")
    }

    val strOrSpace = {
      if (str.isEmpty) " "
      else str
    }

    val (combiningChar, combiningType) = combining(command)
    combiningType match {
      case CombiningType.FirstChar =>
        var i = 1
        // Find the position after the last combining char that decorates the first char
        while (
          i < strOrSpace.length && isCombiningOrControlChar(strOrSpace(i))
        ) {
          i += 1
        }
        // Then insert the new combining char there
        strOrSpace.substring(0, i) + combiningChar + strOrSpace.substring(i)

      case CombiningType.LastChar =>
        strOrSpace + combiningChar

      case CombiningType.EveryChar if str.isEmpty => ""

      case CombiningType.EveryChar =>
        val builder = StringBuilder.newBuilder

        var i = 0
        while (i < str.length) {
          // Push a non-combining char
          builder += str(i)
          i += 1
          // Then push subsequent combining chars that decorates it
          while (i < str.length && isCombiningOrControlChar(str(i))) {
            builder += str(i)
            i += 1
          }
          // Finally insert the new combining char
          builder += combiningChar
        }

        builder.result()
    }
  }

  // \not command

  val not = Map(
    "∃" -> "∄",
    "∈" -> "∉",
    "∋" -> "∌",
    "⊂" -> "⊄",
    "⊃" -> "⊅",
    "⊆" -> "⊈",
    "⊇" -> "⊉",
    "≃" -> "≄",
    "∣" -> "∤",
    "∥" -> "∦",
    "=" -> "≠",
    "≈" -> "≉",
    "≡" -> "≢",
    "<" -> "≮",
    ">" -> "≯",
    "≤" -> "≰",
    "≥" -> "≱",
    "≲" -> "≴",
    "≳" -> "≵",
    "≶" -> "≸",
    "≷" -> "≹",
    "∼" -> "≁",
    "~" -> "≁",
    "≃" -> "≄",
    "⊒" -> "⋣",
    "⊑" -> "⋢",
    "⊴" -> "⋬",
    "⊵" -> "⋭",
    "◁" -> "⋪",
    "▷" -> "⋫",
    "⋞" -> "⋠",
    "⋟" -> "⋡"
  )

  def makeNot(negated: String): String = {
    val s = negated.trim match {
      case ""      => " "
      case trimmed => trimmed
    }
    not.getOrElse(s, s.head + "\u0338" + s.tail)
  }

  // Subscripts

  val subscripts = Map(
    'χ' -> 'ᵪ',
    'φ' -> 'ᵩ',
    'ρ' -> 'ᵨ',
    'γ' -> 'ᵧ',
    'β' -> 'ᵦ',
    'x' -> 'ₓ',
    'v' -> 'ᵥ',
    'u' -> 'ᵤ',
    'r' -> 'ᵣ',
    'o' -> 'ₒ',
    'i' -> 'ᵢ',
    'j' -> 'ⱼ',
    'e' -> 'ₑ',
    'a' -> 'ₐ',
    '=' -> '₌',
    '9' -> '₉',
    '8' -> '₈',
    '7' -> '₇',
    '6' -> '₆',
    '5' -> '₅',
    '4' -> '₄',
    '3' -> '₃',
    '2' -> '₂',
    '1' -> '₁',
    '0' -> '₀',
    '-' -> '₋',
    '−' -> '₋',
    '+' -> '₊',
    ')' -> '₎',
    '(' -> '₍',
    ' ' -> ' '
  )

  def tryMakeSubscript(str: String): Option[String] = {
    if (str.isEmpty) Some("")
    else if (str.forall(subscripts.contains)) Some(str.map(subscripts))
    else None
  }

  def makeSubscript(str: String): String = {
    str.trim match {
      case "" => ""
      case s  => tryMakeSubscript(s).getOrElse(s"_($s)")
    }
  }

  // Superscripts

  val superscripts = Map(
    '∊' -> 'ᵋ',
    'χ' -> 'ᵡ',
    'φ' -> 'ᵠ',
    'ι' -> 'ᶥ',
    'θ' -> 'ᶿ',
    'δ' -> 'ᵟ',
    'γ' -> 'ᵞ',
    'β' -> 'ᵝ',
    'α' -> 'ᵅ',
    'Φ' -> 'ᶲ',
    'z' -> 'ᶻ',
    'y' -> 'ʸ',
    'x' -> 'ˣ',
    'w' -> 'ʷ',
    'v' -> 'ᵛ',
    'u' -> 'ᵘ',
    't' -> 'ᵗ',
    's' -> 'ˢ',
    'r' -> 'ʳ',
    'p' -> 'ᵖ',
    'o' -> 'ᵒ',
    'n' -> 'ⁿ',
    'm' -> 'ᵐ',
    'l' -> 'ˡ',
    'k' -> 'ᵏ',
    'j' -> 'ʲ',
    'i' -> 'ⁱ',
    'h' -> 'ʰ',
    'g' -> 'ᵍ',
    'f' -> 'ᶠ',
    'e' -> 'ᵉ',
    'd' -> 'ᵈ',
    'c' -> 'ᶜ',
    'b' -> 'ᵇ',
    'a' -> 'ᵃ',
    'W' -> 'ᵂ',
    'V' -> 'ⱽ',
    'U' -> 'ᵁ',
    'T' -> 'ᵀ',
    'R' -> 'ᴿ',
    'P' -> 'ᴾ',
    'O' -> 'ᴼ',
    'N' -> 'ᴺ',
    'M' -> 'ᴹ',
    'L' -> 'ᴸ',
    'K' -> 'ᴷ',
    'J' -> 'ᴶ',
    'I' -> 'ᴵ',
    'H' -> 'ᴴ',
    'G' -> 'ᴳ',
    'E' -> 'ᴱ',
    'D' -> 'ᴰ',
    'B' -> 'ᴮ',
    'A' -> 'ᴬ',
    '=' -> '⁼',
    '9' -> '⁹',
    '8' -> '⁸',
    '7' -> '⁷',
    '6' -> '⁶',
    '5' -> '⁵',
    '4' -> '⁴',
    '3' -> '³',
    '2' -> '²',
    '1' -> '¹',
    '0' -> '⁰',
    '-' -> '⁻',
    '−' -> '⁻',
    '+' -> '⁺',
    ')' -> '⁾',
    '(' -> '⁽',
    '∘' -> '°',
    ' ' -> ' '
  )

  def tryMakeSuperScript(str: String): Option[String] = {
    if (str.isEmpty) Some("")
    else if (str.forall(superscripts.contains)) Some(str.map(superscripts))
    else None
  }

  def makeSuperScript(str: String): String = {
    str.trim match {
      case "" => ""
      case s  => tryMakeSuperScript(s).getOrElse(s"^($s)")
    }
  }

  // Styles command

  val bb = Map(
    'z' -> "𝕫",
    'y' -> "𝕪",
    'x' -> "𝕩",
    'w' -> "𝕨",
    'v' -> "𝕧",
    'u' -> "𝕦",
    't' -> "𝕥",
    's' -> "𝕤",
    'r' -> "𝕣",
    'q' -> "𝕢",
    'p' -> "𝕡",
    'o' -> "𝕠",
    'n' -> "𝕟",
    'm' -> "𝕞",
    'l' -> "𝕝",
    'k' -> "𝕜",
    'j' -> "𝕛",
    'i' -> "𝕚",
    'h' -> "𝕙",
    'g' -> "𝕘",
    'f' -> "𝕗",
    'e' -> "𝕖",
    'd' -> "𝕕",
    'c' -> "𝕔",
    'b' -> "𝕓",
    'a' -> "𝕒",
    'Z' -> "ℤ",
    'Y' -> "𝕐",
    'X' -> "𝕏",
    'W' -> "𝕎",
    'V' -> "𝕍",
    'U' -> "𝕌",
    'T' -> "𝕋",
    'S' -> "𝕊",
    'R' -> "ℝ",
    'Q' -> "ℚ",
    'P' -> "ℙ",
    'O' -> "𝕆",
    'N' -> "ℕ",
    'M' -> "𝕄",
    'L' -> "𝕃",
    'K' -> "𝕂",
    'J' -> "𝕁",
    'I' -> "𝕀",
    'H' -> "ℍ",
    'G' -> "𝔾",
    'F' -> "𝔽",
    'E' -> "𝔼",
    'D' -> "𝔻",
    'C' -> "ℂ",
    'B' -> "𝔹",
    'A' -> "𝔸",
    '9' -> "𝟡",
    '8' -> "𝟠",
    '7' -> "𝟟",
    '6' -> "𝟞",
    '5' -> "𝟝",
    '4' -> "𝟜",
    '3' -> "𝟛",
    '2' -> "𝟚",
    '1' -> "𝟙",
    '0' -> "𝟘"
  )

  val bf = Map(
    '∇' -> "𝛁",
    '∂' -> "𝛛",
    'ϵ' -> "𝛜",
    'ϴ' -> "𝚹",
    'ϱ' -> "𝛠",
    'ϰ' -> "𝛞",
    'ϖ' -> "𝛡",
    'ϕ' -> "𝛟",
    'ϑ' -> "𝛝",
    'ω' -> "𝛚",
    'ψ' -> "𝛙",
    'χ' -> "𝛘",
    'φ' -> "𝛗",
    'υ' -> "𝛖",
    'τ' -> "𝛕",
    'σ' -> "𝛔",
    'ς' -> "𝛓",
    'ρ' -> "𝛒",
    'π' -> "𝛑",
    'ο' -> "𝛐",
    'ξ' -> "𝛏",
    'ν' -> "𝛎",
    'μ' -> "𝛍",
    'λ' -> "𝛌",
    'κ' -> "𝛋",
    'ι' -> "𝛊",
    'θ' -> "𝛉",
    'η' -> "𝛈",
    'ζ' -> "𝛇",
    'ε' -> "𝛆",
    'δ' -> "𝛅",
    'γ' -> "𝛄",
    'β' -> "𝛃",
    'α' -> "𝛂",
    'Ω' -> "𝛀",
    'Ψ' -> "𝚿",
    'Χ' -> "𝚾",
    'Φ' -> "𝚽",
    'Υ' -> "𝚼",
    'Τ' -> "𝚻",
    'Σ' -> "𝚺",
    'Ρ' -> "𝚸",
    'Π' -> "𝚷",
    'Ο' -> "𝚶",
    'Ξ' -> "𝚵",
    'Ν' -> "𝚴",
    'Μ' -> "𝚳",
    'Λ' -> "𝚲",
    'Κ' -> "𝚱",
    'Ι' -> "𝚰",
    'Θ' -> "𝚯",
    'Η' -> "𝚮",
    'Ζ' -> "𝚭",
    'Ε' -> "𝚬",
    'Δ' -> "𝚫",
    'Γ' -> "𝚪",
    'Β' -> "𝚩",
    'Α' -> "𝚨",
    'z' -> "𝐳",
    'y' -> "𝐲",
    'x' -> "𝐱",
    'w' -> "𝐰",
    'v' -> "𝐯",
    'u' -> "𝐮",
    't' -> "𝐭",
    's' -> "𝐬",
    'r' -> "𝐫",
    'q' -> "𝐪",
    'p' -> "𝐩",
    'o' -> "𝐨",
    'n' -> "𝐧",
    'm' -> "𝐦",
    'l' -> "𝐥",
    'k' -> "𝐤",
    'j' -> "𝐣",
    'i' -> "𝐢",
    'h' -> "𝐡",
    'g' -> "𝐠",
    'f' -> "𝐟",
    'e' -> "𝐞",
    'd' -> "𝐝",
    'c' -> "𝐜",
    'b' -> "𝐛",
    'a' -> "𝐚",
    'Z' -> "𝐙",
    'Y' -> "𝐘",
    'X' -> "𝐗",
    'W' -> "𝐖",
    'V' -> "𝐕",
    'U' -> "𝐔",
    'T' -> "𝐓",
    'S' -> "𝐒",
    'R' -> "𝐑",
    'Q' -> "𝐐",
    'P' -> "𝐏",
    'O' -> "𝐎",
    'N' -> "𝐍",
    'M' -> "𝐌",
    'L' -> "𝐋",
    'K' -> "𝐊",
    'J' -> "𝐉",
    'I' -> "𝐈",
    'H' -> "𝐇",
    'G' -> "𝐆",
    'F' -> "𝐅",
    'E' -> "𝐄",
    'D' -> "𝐃",
    'C' -> "𝐂",
    'B' -> "𝐁",
    'A' -> "𝐀",
    '9' -> "𝟗",
    '8' -> "𝟖",
    '7' -> "𝟕",
    '6' -> "𝟔",
    '5' -> "𝟓",
    '4' -> "𝟒",
    '3' -> "𝟑",
    '2' -> "𝟐",
    '1' -> "𝟏",
    '0' -> "𝟎"
  )

  val cal = Map(
    'z' -> "𝔃",
    'y' -> "𝔂",
    'x' -> "𝔁",
    'w' -> "𝔀",
    'v' -> "𝓿",
    'u' -> "𝓾",
    't' -> "𝓽",
    's' -> "𝓼",
    'r' -> "𝓻",
    'q' -> "𝓺",
    'p' -> "𝓹",
    'o' -> "𝓸",
    'n' -> "𝓷",
    'm' -> "𝓶",
    'l' -> "𝓵",
    'k' -> "𝓴",
    'j' -> "𝓳",
    'i' -> "𝓲",
    'h' -> "𝓱",
    'g' -> "𝓰",
    'f' -> "𝓯",
    'e' -> "𝓮",
    'd' -> "𝓭",
    'c' -> "𝓬",
    'b' -> "𝓫",
    'a' -> "𝓪",
    'Z' -> "𝓩",
    'Y' -> "𝓨",
    'X' -> "𝓧",
    'W' -> "𝓦",
    'V' -> "𝓥",
    'U' -> "𝓤",
    'T' -> "𝓣",
    'S' -> "𝓢",
    'R' -> "𝓡",
    'Q' -> "𝓠",
    'P' -> "𝓟",
    'O' -> "𝓞",
    'N' -> "𝓝",
    'M' -> "𝓜",
    'L' -> "𝓛",
    'K' -> "𝓚",
    'J' -> "𝓙",
    'I' -> "𝓘",
    'H' -> "𝓗",
    'G' -> "𝓖",
    'F' -> "𝓕",
    'E' -> "𝓔",
    'D' -> "𝓓",
    'C' -> "𝓒",
    'B' -> "𝓑",
    'A' -> "𝓐"
  )

  val frak = Map(
    'z' -> "𝔷",
    'y' -> "𝔶",
    'x' -> "𝔵",
    'w' -> "𝔴",
    'v' -> "𝔳",
    'u' -> "𝔲",
    't' -> "𝔱",
    's' -> "𝔰",
    'r' -> "𝔯",
    'q' -> "𝔮",
    'p' -> "𝔭",
    'o' -> "𝔬",
    'n' -> "𝔫",
    'm' -> "𝔪",
    'l' -> "𝔩",
    'k' -> "𝔨",
    'j' -> "𝔧",
    'i' -> "𝔦",
    'h' -> "𝔥",
    'g' -> "𝔤",
    'f' -> "𝔣",
    'e' -> "𝔢",
    'd' -> "𝔡",
    'c' -> "𝔠",
    'b' -> "𝔟",
    'a' -> "𝔞",
    'Z' -> "ℨ",
    'Y' -> "𝔜",
    'X' -> "𝔛",
    'W' -> "𝔚",
    'V' -> "𝔙",
    'U' -> "𝔘",
    'T' -> "𝔗",
    'S' -> "𝔖",
    'R' -> "ℜ",
    'Q' -> "𝔔",
    'P' -> "𝔓",
    'O' -> "𝔒",
    'N' -> "𝔑",
    'M' -> "𝔐",
    'L' -> "𝔏",
    'K' -> "𝔎",
    'J' -> "𝔍",
    'I' -> "ℑ",
    'H' -> "ℌ",
    'G' -> "𝔊",
    'F' -> "𝔉",
    'E' -> "𝔈",
    'D' -> "𝔇",
    'C' -> "ℭ",
    'B' -> "𝔅",
    'A' -> "𝔄"
  )

  val it = Map(
    '∇' -> "𝛻",
    '∂' -> "𝜕",
    'ϵ' -> "𝜖",
    'ϴ' -> "𝛳",
    'ϱ' -> "𝜚",
    'ϰ' -> "𝜘",
    'ϖ' -> "𝜛",
    'ϕ' -> "𝜙",
    'ϑ' -> "𝜗",
    'ω' -> "𝜔",
    'ψ' -> "𝜓",
    'χ' -> "𝜒",
    'φ' -> "𝜑",
    'υ' -> "𝜐",
    'τ' -> "𝜏",
    'σ' -> "𝜎",
    'ς' -> "𝜍",
    'ρ' -> "𝜌",
    'π' -> "𝜋",
    'ο' -> "𝜊",
    'ξ' -> "𝜉",
    'ν' -> "𝜈",
    'μ' -> "𝜇",
    'λ' -> "𝜆",
    'κ' -> "𝜅",
    'ι' -> "𝜄",
    'θ' -> "𝜃",
    'η' -> "𝜂",
    'ζ' -> "𝜁",
    'ε' -> "𝜀",
    'δ' -> "𝛿",
    'γ' -> "𝛾",
    'β' -> "𝛽",
    'α' -> "𝛼",
    'Ω' -> "𝛺",
    'Ψ' -> "𝛹",
    'Χ' -> "𝛸",
    'Φ' -> "𝛷",
    'Υ' -> "𝛶",
    'Τ' -> "𝛵",
    'Σ' -> "𝛴",
    'Ρ' -> "𝛲",
    'Π' -> "𝛱",
    'Ο' -> "𝛰",
    'Ξ' -> "𝛯",
    'Ν' -> "𝛮",
    'Μ' -> "𝛭",
    'Λ' -> "𝛬",
    'Κ' -> "𝛫",
    'Ι' -> "𝛪",
    'Θ' -> "𝛩",
    'Η' -> "𝛨",
    'Ζ' -> "𝛧",
    'Ε' -> "𝛦",
    'Δ' -> "𝛥",
    'Γ' -> "𝛤",
    'Β' -> "𝛣",
    'Α' -> "𝛢",
    'z' -> "𝑧",
    'y' -> "𝑦",
    'x' -> "𝑥",
    'w' -> "𝑤",
    'v' -> "𝑣",
    'u' -> "𝑢",
    't' -> "𝑡",
    's' -> "𝑠",
    'r' -> "𝑟",
    'q' -> "𝑞",
    'p' -> "𝑝",
    'o' -> "𝑜",
    'n' -> "𝑛",
    'm' -> "𝑚",
    'l' -> "𝑙",
    'k' -> "𝑘",
    'j' -> "𝑗",
    'i' -> "𝑖",
    'h' -> "ℎ",
    'g' -> "𝑔",
    'f' -> "𝑓",
    'e' -> "𝑒",
    'd' -> "𝑑",
    'c' -> "𝑐",
    'b' -> "𝑏",
    'a' -> "𝑎",
    'Z' -> "𝑍",
    'Y' -> "𝑌",
    'X' -> "𝑋",
    'W' -> "𝑊",
    'V' -> "𝑉",
    'U' -> "𝑈",
    'T' -> "𝑇",
    'S' -> "𝑆",
    'R' -> "𝑅",
    'Q' -> "𝑄",
    'P' -> "𝑃",
    'O' -> "𝑂",
    'N' -> "𝑁",
    'M' -> "𝑀",
    'L' -> "𝐿",
    'K' -> "𝐾",
    'J' -> "𝐽",
    'I' -> "𝐼",
    'H' -> "𝐻",
    'G' -> "𝐺",
    'F' -> "𝐹",
    'E' -> "𝐸",
    'D' -> "𝐷",
    'C' -> "𝐶",
    'B' -> "𝐵",
    'A' -> "𝐴"
  )

  val tt = Map(
    'z' -> "𝚣",
    'y' -> "𝚢",
    'x' -> "𝚡",
    'w' -> "𝚠",
    'v' -> "𝚟",
    'u' -> "𝚞",
    't' -> "𝚝",
    's' -> "𝚜",
    'r' -> "𝚛",
    'q' -> "𝚚",
    'p' -> "𝚙",
    'o' -> "𝚘",
    'n' -> "𝚗",
    'm' -> "𝚖",
    'l' -> "𝚕",
    'k' -> "𝚔",
    'j' -> "𝚓",
    'i' -> "𝚒",
    'h' -> "𝚑",
    'g' -> "𝚐",
    'f' -> "𝚏",
    'e' -> "𝚎",
    'd' -> "𝚍",
    'c' -> "𝚌",
    'b' -> "𝚋",
    'a' -> "𝚊",
    'Z' -> "𝚉",
    'Y' -> "𝚈",
    'X' -> "𝚇",
    'W' -> "𝚆",
    'V' -> "𝚅",
    'U' -> "𝚄",
    'T' -> "𝚃",
    'S' -> "𝚂",
    'R' -> "𝚁",
    'Q' -> "𝚀",
    'P' -> "𝙿",
    'O' -> "𝙾",
    'N' -> "𝙽",
    'M' -> "𝙼",
    'L' -> "𝙻",
    'K' -> "𝙺",
    'J' -> "𝙹",
    'I' -> "𝙸",
    'H' -> "𝙷",
    'G' -> "𝙶",
    'F' -> "𝙵",
    'E' -> "𝙴",
    'D' -> "𝙳",
    'C' -> "𝙲",
    'B' -> "𝙱",
    'A' -> "𝙰",
    '9' -> "𝟿",
    '8' -> "𝟾",
    '7' -> "𝟽",
    '6' -> "𝟼",
    '5' -> "𝟻",
    '4' -> "𝟺",
    '3' -> "𝟹",
    '2' -> "𝟸",
    '1' -> "𝟷",
    '0' -> "𝟶"
  )

  val styles = Map(
    "\\mathbb" -> bb,
    "\\textbb" -> bb,
    "\\mathbf" -> bf,
    "\\textbf" -> bf,
    "\\mathcal" -> cal,
    "\\textcal" -> cal,
    "\\mathfrak" -> frak,
    "\\textfrak" -> frak,
    "\\mathit" -> it,
    "\\textit" -> it,
    "\\mathtt" -> tt,
    "\\texttt" -> tt
  )

  def isStylesCommand(command: String): Boolean = styles.contains(command)

  def translateStyles(command: String, str: String): String = {
    if (!isStylesCommand(command)) {
      throw new RuntimeException(s"Unknown styles command: $command")
    }

    val map = styles(command)
    str.map(c => map.getOrElse(c, c.toString)).mkString
  }

  // Common helper interface

  val names: Set[String] = Set(
    "\\not",
    "_",
    "^",
    "\\textsubscript",
    "\\textsuperscript"
  ) ++ combining.keys ++ styles.keys

  def translate(command: String, param: String): String = {
    if (!names.contains(command)) {
      throw new IllegalArgumentException(s"Unknown command: $command")
    }

    command match {
      case "_" | "\\textsubscript"   => makeSubscript(param)
      case "^" | "\\textsuperscript" => makeSuperScript(param)
      case "\\not"                   => makeNot(param)
      case _ if isCombiningCommand(command) =>
        translateCombining(command, param)
      case _ if isStylesCommand(command) => translateStyles(command, param)
    }
  }

}
