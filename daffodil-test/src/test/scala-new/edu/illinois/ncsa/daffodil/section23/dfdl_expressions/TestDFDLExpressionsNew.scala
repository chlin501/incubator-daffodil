package edu.illinois.ncsa.daffodil.section23.dfdl_expressions

/* Copyright (c) 2012-2013 Tresys Technology, LLC. All rights reserved.
 *
 * Developed by: Tresys Technology, LLC
 *               http://www.tresys.com
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal with
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 * 
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimers.
 * 
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimers in the
 *     documentation and/or other materials provided with the distribution.
 * 
 *  3. Neither the names of Tresys Technology, nor the names of its contributors
 *     may be used to endorse or promote products derived from this Software
 *     without specific prior written permission.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * CONTRIBUTORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS WITH THE
 * SOFTWARE.
 */

import junit.framework.Assert._
import org.junit.Test
import scala.xml._
import edu.illinois.ncsa.daffodil.xml.XMLUtils
import edu.illinois.ncsa.daffodil.xml.XMLUtils._
import edu.illinois.ncsa.daffodil.compiler.Compiler
import edu.illinois.ncsa.daffodil.util._
import edu.illinois.ncsa.daffodil.tdml.DFDLTestSuite
import java.io.File
import edu.illinois.ncsa.daffodil.debugger.Debugger

class TestDFDLExpressionsNew {

  val testDir = "/edu/illinois/ncsa/daffodil/section23/dfdl_expressions/"

  val testDir3 = "/edu/illinois/ncsa/daffodil/section23/runtime_properties/"
  val rp = testDir3 + "runtime-properties.tdml"
  lazy val runner3 = new DFDLTestSuite(Misc.getRequiredResource(rp))

  val testDir2 = "/edu/illinois/ncsa/daffodil/section23/dfdl_functions/"
  val aa = testDir2 + "Functions.tdml"
  lazy val runner2 = new DFDLTestSuite(Misc.getRequiredResource(aa))

  val tdml2 = testDir + "functions.tdml"
  lazy val runner_fun = new DFDLTestSuite(Misc.getRequiredResource(tdml2))

  @Test def test_substringFunction01() { runner_fun.runOneTest("substringFunction01") }
  @Test def test_substringFunction02() { runner_fun.runOneTest("substringFunction02") }
  @Test def test_substringFunction03() { runner_fun.runOneTest("substringFunction03") }
  @Test def test_substringFunction04() { runner_fun.runOneTest("substringFunction04") }
  @Test def test_substringFunction05() { runner_fun.runOneTest("substringFunction05") }
  @Test def test_substringFunction06() { runner_fun.runOneTest("substringFunction06") }
  @Test def test_substringFunction07() { runner_fun.runOneTest("substringFunction07") }
  @Test def test_substringFunction08() { runner_fun.runOneTest("substringFunction08") }
  // Fails fn:substring((), 1, 3) returns "".
  //    @Test def test_substringFunction09() { runner_fun.runOneTest("substringFunction09") }
  @Test def test_substringFunction10() { runner_fun.runOneTest("substringFunction10") }
  @Test def test_substringFunction11() { runner_fun.runOneTest("substringFunction11") }
  @Test def test_substringFunction12() { runner_fun.runOneTest("substringFunction12") }
  @Test def test_substringFunction13() { runner_fun.runOneTest("substringFunction13") }
  @Test def test_substringFunction14() { runner_fun.runOneTest("substringFunction14") }
  @Test def test_substringFunction15() { runner_fun.runOneTest("substringFunction15") }

  @Test def test_xPathFunc_round_14() { runner2.runOneTest("xPathFunc_round_14") }
  @Test def test_xPathFunc_round_15() { runner2.runOneTest("xPathFunc_round_15") }

  // DFDL-1093: DPath: fn:starts-with() Unsupported Function
  @Test def test_startswith_01() { runner2.runOneTest("startswith_01") }
  @Test def test_startswith_02() { runner2.runOneTest("startswith_02") }
  @Test def test_startswith_03() { runner2.runOneTest("startswith_03") }
  @Test def test_startswith_04() { runner2.runOneTest("startswith_04") }
  @Test def test_startswith_05() { runner2.runOneTest("startswith_05") }
  @Test def test_startswith_06() { runner2.runOneTest("startswith_06") }
  @Test def test_startswith_07() { runner2.runOneTest("startswith_07") }

  // DFDL-1090: DPath: fn:contains() Unsupported Function
  @Test def test_contains_01() { runner2.runOneTest("contains_01") }
  @Test def test_contains_02() { runner2.runOneTest("contains_02") }
  @Test def test_contains_03() { runner2.runOneTest("contains_03") }
  @Test def test_contains_04() { runner2.runOneTest("contains_04") }
  @Test def test_contains_05() { runner2.runOneTest("contains_05") }
  @Test def test_contains_06() { runner2.runOneTest("contains_06") }
  @Test def test_contains_07() { runner2.runOneTest("contains_07") }
  @Test def test_boolFlags_03() { runner2.runOneTest("boolFlags_03") }
  @Test def test_boolFlags_04() { runner2.runOneTest("boolFlags_04") }
  @Test def test_boolFlags_05() { runner2.runOneTest("boolFlags_05") }

  // DFDL-1092: DPath: fn:substring-before()
  @Test def test_substringbefore_01 { runner2.runOneTest("substringbefore_01") }
  @Test def test_substringbefore_02 { runner2.runOneTest("substringbefore_02") }
  @Test def test_substringbefore_03 { runner2.runOneTest("substringbefore_03") }
  @Test def test_substringbefore_04 { runner2.runOneTest("substringbefore_04") }
  @Test def test_substringbefore_05 { runner2.runOneTest("substringbefore_05") }
  @Test def test_substringbefore_06 { runner2.runOneTest("substringbefore_06") }
  @Test def test_substringbefore_07 { runner2.runOneTest("substringbefore_07") }
  
  // DFDL-1094: DPath: fn:substring-after()
  @Test def test_substringafter_01()  { runner2.runOneTest("substringafter_01") }
  @Test def test_substringafter_02()  { runner2.runOneTest("substringafter_02") }
  @Test def test_substringafter_03()  { runner2.runOneTest("substringafter_03") }
  @Test def test_substringafter_04()  { runner2.runOneTest("substringafter_04") }
  @Test def test_substringafter_05()  { runner2.runOneTest("substringafter_05") }
  @Test def test_substringafter_06()  { runner2.runOneTest("substringafter_06") }
  @Test def test_substringafter_07()  { runner2.runOneTest("substringafter_07") }

}
