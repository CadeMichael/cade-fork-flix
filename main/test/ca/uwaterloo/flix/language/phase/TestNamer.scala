/*
 * Copyright 2015-2016 Magnus Madsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.uwaterloo.flix.language.phase

import ca.uwaterloo.flix.TestUtils
import ca.uwaterloo.flix.language.errors.{NameError, ResolutionError}
import ca.uwaterloo.flix.util.Options
import org.scalatest.funsuite.AnyFunSuite

class TestNamer extends AnyFunSuite with TestUtils {

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateImport.01") {
    val input =
      """
        |import java.lang.StringBuffer
        |import java.lang.StringBuffer
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateImport.02") {
    val input =
      """
        |import java.lang.{StringBuffer => StringThingy}
        |import java.lang.{StringBuffer => StringThingy}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateImport.03") {
    val input =
      """
        |mod A {
        |    import java.lang.StringBuffer
        |    import java.lang.StringBuffer
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateImport.04") {
    val input =
      """
        |mod A {
        |    import java.lang.{StringBuffer => StringThingy}
        |    import java.lang.{StringBuilder => StringThingy}
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateLowerName.01") {
    val input =
      s"""
         |def f(): Int = 42
         |def f(): Int = 21
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.02") {
    val input =
      s"""
         |def f(): Int = 42
         |def f(): Int = 21
         |def f(): Int = 11
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.03") {
    val input =
      s"""
         |def f(x: Int): Int = 42
         |def f(x: Int): Int = 21
         |def f(x: Int): Int = 11
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.04") {
    val input =
      s"""
         |def f(): Int = 42
         |def f(x: Int): Int = 21
         |def f(x: Bool, y: Int, z: String): Int = 11
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.05") {
    val input =
      s"""
         |mod A {
         |  def f(): Int = 42
         |}
         |
         |mod A {
         |  def f(): Int = 21
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.06") {
    val input =
      s"""
         |mod A.B.C {
         |  def f(): Int = 42
         |}
         |
         |mod A {
         |  mod B {
         |    mod C {
         |      def f(): Int = 21
         |    }
         |  }
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.07") {
    val input =
      """
        |trait C[a] {
        |    pub def f(x: a): Int
        |    pub def f(x: a): Bool
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.08") {
    val input =
      """
        |trait C[a] {
        |    pub def f(x: a): Int
        |    pub def f(x: a): Bool
        |    pub def f(x: Int): a
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.09") {
    val input =
      s"""
         |trait A[a] {
         |  pub def f(x: a): Int
         |}
         |
         |mod A {
         |  pub def f(): Int = 21
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.10") {
    val input =
      s"""
         |mod A.B.C {
         |  def f(): Int = 42
         |}
         |
         |mod A {
         |  mod B {
         |    trait C[a] {
         |      pub def f(x: a): Int
         |    }
         |  }
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.11") {
    val input =
      s"""
         |mod A.C {
         |  def f(): Int = 42
         |}
         |
         |mod A {
         |  trait C[a] {
         |    pub def f(x: a): Int
         |  }
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.12") {
    val input =
      """
        |mod N {
        |    def f(): Int32 = 123
        |}
        |
        |eff N {
        |    pub def f(): Unit
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateLowerName.13") {
    val input =
      """
        |eff N {
        |    pub def f(): Unit
        |    pub def f(): Unit
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateLowerName](result)
  }

  test("DuplicateUpperName.01") {
    val input =
      s"""
         |type alias USD = Int
         |type alias USD = Int
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.02") {
    val input =
      s"""
         |type alias USD = Int
         |type alias USD = Int
         |type alias USD = Int
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.03") {
    val input =
      s"""
         |mod A {
         |  type alias USD = Int
         |}
         |
         |mod A {
         |  type alias USD = Int
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.04") {
    val input =
      s"""
         |type alias USD = Int
         |enum USD {
         |  case A
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.05") {
    val input =
      s"""
         |type alias USD = Int
         |type alias USD = Int
         |enum USD {
         |  case A
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.06") {
    val input =
      s"""
         |mod A {
         |  type alias USD = Int
         |}
         |
         |mod A {
         |  enum USD {
         |    case B
         |  }
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.07") {
    val input =
      s"""
         |enum USD {
         |  case A
         |}
         |enum USD {
         |  case B
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.08") {
    val input =
      s"""
         |enum USD {
         |  case A
         |}
         |enum  USD {
         |  case B
         |}
         |enum USD {
         |  case C
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.09") {
    val input =
      s"""
         |mod A {
         |  enum USD {
         |    case A
         |  }
         |}
         |
         |mod A {
         |  enum USD {
         |    case B
         |  }
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.10") {
    val input =
      s"""
         |type alias USD = Int
         |trait USD[a]
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.11") {
    val input =
      s"""
         |type alias USD = Int
         |type alias USD = Int
         |trait USD[a]
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.12") {
    val input =
      s"""
         |mod A {
         |  type alias USD = Int
         |}
         |
         |mod A {
         |  trait USD[a]
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.13") {
    val input =
      s"""
         |enum USD {
         |  case A
         |}
         |trait USD[a]
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.14") {
    val input =
      s"""
         |enum USD {
         |  case A
         |}
         |enum USD {
         |  case B
         |}
         |trait USD[a]
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.15") {
    val input =
      s"""
         |mod A {
         |  enum USD {
         |    case A
         |  }
         |}
         |
         |mod A {
         |  trait USD[a]
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.16") {
    val input =
      s"""
         |trait USD[a]
         |trait USD[a]
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.17") {
    val input =
      s"""
         |trait USD[a]
         |trait USD[a]
         |trait USD[a]
         """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.18") {
    val input =
      s"""
         |mod A {
         |  trait USD[a]
         |}
         |
         |mod A {
         |  trait USD[a]
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.19") {
    val input =
      """
        |enum E
        |eff E
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.20") {
    val input =
      """
        |trait C[a]
        |eff C
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateUpperName.21") {
    val input =
      """
        |import java.sql.Statement
        |enum Statement
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.22") {
    val input =
      """
        |enum Statement
        |type alias Statement = Int
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateUpperName.23") {
    val input =
      """
        |use A.Statement
        |enum Statement
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateUpperName.24") {
    val input =
      """
        |mod A {
        |    import java.sql.Statement
        |    enum Statement
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateUpperName.25") {
    val input =
      """
        |mod A {
        |    use B.Statement
        |    import java.sql.Statement
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateUpperName.26") {
    val input =
      """
        |enum Statement
        |mod A {
        |    use B.Statement
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  // TODO NS-REFACTOR move to Redundancy
  ignore("DuplicateUpperName.27") {
    val input =
      """
        |enum Statement
        |mod A {
        |    import B.Statement
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.28") {
    val input =
      """
        |struct S[r] {}
        |enum S {}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.Tag.01") {
    val input =
      """enum Color {
        |  case Red,
        |  case Red
        |}
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.Tag.02") {
    val input =
      """enum Color {
        |  case Red,
        |  case Blu,
        |  case Red
        |}
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.Tag.03") {
    val input =
      """enum Color {
        |  case Red,
        |  case Blu,
        |  case Red
        |  case Blu
        |}
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("DuplicateUpperName.Tag.04") {
    val input =
      """enum Color {
        |  case Red,
        |  case Blu,
        |  case Blu,
        |  case Ylw
        |}
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.DuplicateUpperName](result)
  }

  test("IllegalReservedName.Enum.01") {
    val input =
      """pub enum Int32 {
        |  case Value,
        |}
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.IllegalReservedName](result)
  }
  
  test("IllegalReservedName.Alias.01") {
    val input =
      """type alias Float32[k] = (k,k)
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.IllegalReservedName](result)
  }

  test("IllegalReservedName.Struct.01") {
    val input =
      """struct Float64 {}
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.IllegalReservedName](result)
  }

  test("IllegalReservedName.Trait.01") {
    val input =
      """trait String[s] { def f(x: s): s }
    """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.IllegalReservedName](result)
  }

  test("SuspiciousTypeVarName.01") {
    val input =
      s"""
         |def f(_x: List[unit]): Unit = ()
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.SuspiciousTypeVarName](result)
  }

  test("SuspiciousTypeVarName.02") {
    val input =
      s"""
         |def f(_x: List[Result[Unit, bool]]): Unit = ()
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.SuspiciousTypeVarName](result)
  }

  test("SuspiciousTypeVarName.03") {
    val input =
      s"""
         |def f(): List[char] = ()
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.SuspiciousTypeVarName](result)
  }

  test("SuspiciousTypeVarName.04") {
    val input =
      s"""
         |enum A {
         |    case X(string)
         |}
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[NameError.SuspiciousTypeVarName](result)
  }

  test("Regression.01") {
    // See https://github.com/flix/flix/issues/10116
    // We ensure that duplicate names do not cause unrelated resolution errors
    // We use the whole standard library to maximize diversity of resolutions.
    val input =
      """
        |def foo(): Unit = ()
        |def foo(): Unit = ()
        |""".stripMargin
    val result = compile(input, Options.TestWithLibAll)
    expectError[NameError.DuplicateLowerName](result)
    rejectError[ResolutionError.UndefinedUse](result)
  }
}
