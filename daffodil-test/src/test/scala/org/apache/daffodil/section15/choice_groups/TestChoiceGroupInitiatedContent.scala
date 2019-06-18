/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.daffodil.section15.choice_groups

import org.junit.Test
import org.apache.daffodil.tdml.Runner
import org.junit.AfterClass

object TestChoiceGroupInitiatedContent {
  val testDir_01 = "/org/apache/daffodil/section15/choice_groups/"
  val runner_01 = Runner(testDir_01, "ChoiceGroupInitiatedContent.tdml")

  @AfterClass def shutDown {
    runner_01.reset
  }

}

class TestChoiceGroupInitiatedContent {

  import TestChoiceGroupInitiatedContent._

  @Test def test_initiatedContentChoice1() { runner_01.runOneTest("initiatedContentChoice1") }
  @Test def test_initiatedContentChoice2() { runner_01.runOneTest("initiatedContentChoice2") }
  @Test def test_initiatedContentChoice3() { runner_01.runOneTest("initiatedContentChoice3") }
  @Test def test_initiatedContentChoice4() { runner_01.runOneTest("initiatedContentChoice4") }
  @Test def test_initiatedContentChoice5() { runner_01.runOneTest("initiatedContentChoice5") }
  @Test def test_initiatedContentChoice6() { runner_01.runOneTest("initiatedContentChoice6") }
  @Test def test_initiatedContentChoice7() { runner_01.runOneTest("initiatedContentChoice7") }
  @Test def test_initiatedContentChoice8() { runner_01.runOneTest("initiatedContentChoice8") }
  @Test def test_initiatedContentChoice9() { runner_01.runOneTest("initiatedContentChoice9") }
  @Test def test_initiatedContentChoice10() { runner_01.runOneTest("initiatedContentChoice10") }

  // Test for DAFFODIL-2143
  @Test def test_arrayOptionalChildDiscriminatesElementAndChoice1() { runner_01.runOneTest("arrayOptionalChildDiscriminatesElementAndChoice1") }

  @Test def test_arrayOfChoice() { runner_01.runOneTest("arrayOfChoice") }
  @Test def test_arrayOfChoice2() { runner_01.runOneTest("arrayOfChoice2") }
  @Test def test_discriminatorNesting1() { runner_01.runOneTest("discriminatorNesting1") }
  @Test def test_discriminatorNesting2() { runner_01.runOneTest("discriminatorNesting2") }
  @Test def test_Lesson5_choice_state() { runner_01.runOneTest("Lesson5_choice_state") }
  @Test def test_Lesson5_choice_county() { runner_01.runOneTest("Lesson5_choice_county") }
  @Test def test_Lesson5_choice_province() { runner_01.runOneTest("Lesson5_choice_province") }

  @Test def test_unparse_initiatedContentChoice1() { runner_01.runOneTest("unparse_initiatedContentChoice1") }
  @Test def test_initiatedContentNestedChoices1() { runner_01.runOneTest("initiatedContentNestedChoices1") }
  @Test def test_initiatedContentNestedChoices2() { runner_01.runOneTest("initiatedContentNestedChoices2") }
}
