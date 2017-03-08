/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import uk.gov.hmrc.play.test.UnitSpec

class AgentSubmissionModelSpec extends UnitSpec {

  "Creating an agent submission model" which {

    "has an invalid arn" when {

      "the arn does not start with a valid character" should {
        val arn = "ARN1234567"
        val sap = "123456789098765"
        lazy val ex = intercept[Exception] {
          AgentSubmissionModel(sap, arn)
        }

        "throw an exception" in {
          ex.getMessage shouldBe s"requirement failed: ARN:$arn is not valid."
        }
      }

      "the arn does not contain 'ARN'" should {
        val arn = "A1234567"
        val sap = "123456789098765"
        lazy val ex = intercept[Exception] {
          AgentSubmissionModel(sap, arn)
        }

        "throw an exception" in {
          ex.getMessage shouldBe s"requirement failed: ARN:$arn is not valid."
        }
      }

      "the arn uses non-numeric digits" should {
        val arn = "CARN123R567"
        val sap = "123456789098765"
        lazy val ex = intercept[Exception] {
          AgentSubmissionModel(sap, arn)
        }

        "throw an exception" in {
          ex.getMessage shouldBe s"requirement failed: ARN:$arn is not valid."
        }
      }

      "the arn does not have 7 numeric digits" should {
        val arn = "CARN123456"
        val sap = "123456789098765"
        lazy val ex = intercept[Exception] {
          AgentSubmissionModel(sap, arn)
        }

        "throw an exception" in {
          ex.getMessage shouldBe s"requirement failed: ARN:$arn is not valid."
        }
      }
    }

    "has an invalid sap" should {
      val arn = "CARN1234567"
      val sap = "12345678909876"
      lazy val ex = intercept[Exception] {
        AgentSubmissionModel(sap, arn)
      }

      "throw an exception" in {
        ex.getMessage shouldBe s"requirement failed: SAP:$sap is not valid."
      }
    }
  }
}
