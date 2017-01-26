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
import models.{Identifier, SubscriptionIssuerRequest, SubscriptionSubscriberRequest}
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import repository.{CGTMongoRepository, CGTRepository, SubscriptionTaxEnrolmentConnector}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import org.mockito.Mockito._

import scala.concurrent.Future

@Singleton
class TaxEnrolmentsControllerControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(addEntryResult: Future[Unit]): TaxEnrolmentsController ={
    val mockIssuerRepository = mock[CGTMongoRepository[SubscriptionIssuerRequest, Identifier]]
    val mockSubscriberRepository = mock[CGTMongoRepository[SubscriptionSubscriberRequest, String]]

    val mockConnector = mock[SubscriptionTaxEnrolmentConnector]
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
        val validSubscribeIssuer = SubscriptionIssuerRequest("cgt", Identifier("nino", "randomNino"))
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
        val validSubscriberSubscriber = SubscriptionSubscriberRequest("CGT", "http://google.com", "id")
        lazy val result = await(controller.subscribeSubscriber("sap")(FakeRequest("PUT", "").withJsonBody(Json.toJson(validSubscriberSubscriber))))

        status(result) shouldBe 204
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

}