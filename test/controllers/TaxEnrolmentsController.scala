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

package controllers

import com.google.inject.{Inject, Singleton}
import org.scalatest.mock.MockitoSugar
import repository.SubscriptionTaxEnrolmentConnector
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

@Singleton
class TaxEnrolmentsControllerControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(): Unit ={

  }

  "Calling .subscribeIssue" when {
    "a valid request is submitted" should {
      "return a status of 204" in {

      }
    }

    "an invalid request is submitted" should {
      "return a status of 400" in {

      }
    }
  }

  "Calling .subscribeSubscriber" when {
    "a valid request is submitted" should {
      "return a status of 204" in {

      }

      "an invalid request is submitted" should {
        "return a status of 400" in {

        }
      }
    }
  }

}