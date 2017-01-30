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

import org.scalatest.mock.MockitoSugar
import repository.{BPMongoConnector, SubscriptionMongoConnector, TaxEnrolmentConnector}
import uk.gov.hmrc.play.microservice.controller.BaseController
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class ClearDownControllerSpec extends UnitSpec with BaseController with WithFakeApplication with MockitoSugar {

  def setupController(): ClearDownController = {

    val mockRegistrationConnector = mock[BPMongoConnector]
    val mockSubscriptionConnector = mock[SubscriptionMongoConnector]
    val mockEnrolmentConnector = mock[TaxEnrolmentConnector]

    new ClearDownController(mockRegistrationConnector, mockSubscriptionConnector, mockEnrolmentConnector)
  }

  private val successful = Ok("Success")
  private val failed = BadRequest("Could not delete data")

  "Calling .checkForFailed" should {

    val controller = setupController()
    val successfulClearDown = Seq(successful, successful, successful, successful)
    val oneFailedClearDown = Seq(successful, failed, successful, successful)
    val twoFailedClearDowns = Seq(failed, successful, successful, failed)
    val threeFailedClearDowns = Seq(failed, failed, successful, failed)
    val fourFailedClearDowns = Seq(failed, failed, failed, failed)

    "when all the clears succeed" in {
      await(controller.checkForFailed(successfulClearDown)) shouldEqual true
    }

    "when one of the clears fails" in {
      await(controller.checkForFailed(oneFailedClearDown)) shouldEqual false
    }

    "when two of the clears fail" in {
      await(controller.checkForFailed(twoFailedClearDowns)) shouldEqual false
    }

    "when three of the clears fail" in {
      await(controller.checkForFailed(threeFailedClearDowns)) shouldEqual false
    }

    "when all four of the clears fail" in {
      await(controller.checkForFailed(fourFailedClearDowns)) shouldEqual false
    }
  }
}
