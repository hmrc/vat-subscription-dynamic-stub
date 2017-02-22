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

import javax.inject.Singleton

import actions.ExceptionTriggersActions
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import repositories._
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

@Singleton
class TaxEnrolmentsControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(addEntryResult: Future[Unit], expectedExceptionCode: Option[Int] = None): TaxEnrolmentsController = {
    val mockIssuerCollection = mock[CgtRepository[EnrolmentIssuerRequestModel, Identifier]]
    val mockIssuerRepository = mock[TaxEnrolmentIssuerRepository]

    val mockSubscriberCollection = mock[CgtRepository[EnrolmentSubscriberRequestModel, String]]
    val mockSubscriberRepository = mock[TaxEnrolmentSubscriberRepository]

    val mockExceptionsCollection = mock[CgtRepository[RouteExceptionModel, RouteExceptionKeyModel]]
    val mockExceptionRepository = mock[RouteExceptionRepository]
    val exceptionTriggerActions = new ExceptionTriggersActions(mockExceptionRepository)
    val expectedException = expectedExceptionCode.fold(List[RouteExceptionModel]()) {
      code => List(RouteExceptionModel("", "", code))
    }


    when(mockIssuerRepository.apply()).thenReturn(mockIssuerCollection)
    when(mockSubscriberRepository.apply()).thenReturn(mockSubscriberCollection)

    when(mockIssuerCollection.addEntry(any())(any()))
      .thenReturn(addEntryResult)

    when(mockSubscriberCollection.addEntry(any())(any()))
      .thenReturn(addEntryResult)

    when(mockExceptionRepository.apply())
          .thenReturn(mockExceptionsCollection)

    when(mockExceptionsCollection.findLatestVersionBy(any())(any()))
          .thenReturn(Future.successful(expectedException))

    new TaxEnrolmentsController(mockSubscriberRepository, mockIssuerRepository, exceptionTriggerActions)
  }

  "Calling .subscribeIssue" when {
    "a valid request is submitted" should {
      "return a status of 204" in {
        val controller = setupController(Future.successful({}))
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
        val controller = setupController(Future.successful({}))
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