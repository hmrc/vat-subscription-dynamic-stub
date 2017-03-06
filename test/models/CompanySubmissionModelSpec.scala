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

class CompanySubmissionModelSpec extends UnitSpec {

  "Creating a company submission model" which {

    "has an invalid sap" should {
      val sap = Some("123456789")
      lazy val ex = intercept[Exception] {
        CompanySubmissionModel(sap, None, None)
      }

      "throw an exception" in {
        ex.getMessage shouldBe s"requirement failed: SAP:$sap is not valid."
      }
    }
  }
}
