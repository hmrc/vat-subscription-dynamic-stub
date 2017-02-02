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

package controllers.stubs

import com.google.inject.Singleton
import models.{EnrolmentIssuerRequestModel, EnrolmentSubscriberRequestModel, Identifier}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import repository.{EnrolmentMongoRepository, TaxEnrolmentConnector}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

@Singleton
class TaxEnrolmentsControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(addEntryResult: Future[Unit]): TaxEnrolmentsController = {
    val mockIssuerRepository = mock[EnrolmentMongoRepository[EnrolmentIssuerRequestModel, Identifier]]
    val mockSubscriberRepository = mock[EnrolmentMongoRepository[EnrolmentSubscriberRequestModel, String]]

    val mockConnector = mock[TaxEnrolmentConnector]
    new TaxEnrolmentsController(mockConnector)

    when(mockConnector.issuerRepository).thenReturn(mockIssuerRepository)

    when(mockConnector.subscriberRepository).thenReturn(mockSubscriberRepository)

    when(mockIssuerRepository.addEntry(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(addEntryResult)

    when(mockSubscriberRepository.addEntry(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(addEntryResult)

    new TaxEnrolmentsController(mockConnector)
  }

  "Calling .subscribeIssue" when {
    "a valid request is submitted" should {
      "return a status of 204" in {
        val controller = setupController(Future.successful())
        val validSubscribeIssuer = EnrolmentIssuerRequestModel("cgt", Identifier("nino", "randomNino"))
        lazy val result = await(controller.subscribeIssuer("sap")(FakeRequest("PUT", "").withJsonBody(Json.toJson(validSubscribeIssuer))))

        status(result) shouldBe 204
      }
    }

    "an invalid request is submitted" should {
      "return a status of 400" in {
        val controller = setupController(Future.failed(new Exception))
        val invalidSubscriberIssue = "cast me to json please"
        lazy val result = await(controller.subscribeIssuer("sap")(FakeRequest("PUT", "").withJsonBody(Json.toJson(invalidSubscriberIssue))))

        status(result) shouldBe 400
      }
    }
  }

  "Calling .subscribeSubscriber" when {
    "a valid request is submitted" should {
      "return a status of 204" in {
        val controller = setupController(Future.successful())
        val validSubscriberSubscriber = EnrolmentSubscriberRequestModel("CGT", "http://google.com", "id")
        lazy val result = await(controller.subscribeSubscriber("sap")(FakeRequest("PUT", "").withJsonBody(Json.toJson(validSubscriberSubscriber))))

        status(result) shouldBe 204
      }
    }

    "an invalid request is submitted" should {
      "return a status of 400" in {
        val controller = setupController(Future.failed(new Exception))
        val invalidSubscriberSubscriber = "cast me to json please"
        lazy val result = await(controller.subscribeIssuer("sap")(FakeRequest("PUT", "").withJsonBody(Json.toJson(invalidSubscriberSubscriber))))

        status(result) shouldBe 400
      }
    }
  }
}