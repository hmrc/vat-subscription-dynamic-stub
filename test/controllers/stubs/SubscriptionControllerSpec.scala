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

import actions.SAPExceptionTriggers
import helpers.CGTRefHelper
import models.{SubscribeModel, SubscriberModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repository.{CGTMongoRepository, SubscriptionMongoConnector}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class SubscriptionControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(findLatestVersionResult: Future[List[SubscriberModel]], addEntryResult: Future[Unit], ref: String) = {

    val mockRepository = mock[CGTMongoRepository[SubscriberModel, String]]
    val mockConnector = mock[SubscriptionMongoConnector]
    val mockCGTRefHelper = mock[CGTRefHelper]
    def exceptionTriggersActions = fakeApplication.injector.instanceOf[SAPExceptionTriggers]

    when(mockConnector.repository)
      .thenReturn(mockRepository)

    when(mockRepository.addEntry(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(addEntryResult)

    when(mockRepository.findLatestVersionBy(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockCGTRefHelper.generateCGTReference())
      .thenReturn(ref)

    new SubscriptionController(mockConnector, mockCGTRefHelper, exceptionTriggersActions)
  }

  "Calling subscribe" when {

    "a list with subscribers is returned" should {
      val controller = setupController(Future.successful(List(SubscriberModel("123456789", "CGT123456"))), Future.successful(()), "CGT654321")
      lazy val result = controller.subscribe("123456789")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(SubscribeModel("123456789"))))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "CGT123456"
      }
    }

    "a list with no subscribers is returned" should {
      val controller = setupController(Future.successful(List()), Future.successful(()), "CGT654321")
      lazy val result = controller.subscribe("123456789")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(SubscribeModel("123456789"))))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "CGT654321"
      }
    }

    "an error matching safe id is detected" should {
      val controller = setupController(Future.successful(List(SubscriberModel("123456789", "CGT123456"))), Future.successful(()), "CGT654321")
      lazy val result = controller.subscribe("404404404")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(SubscribeModel("404404404"))))

      "return a status of 404" in {
        status(result) shouldBe 404
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return an error code" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "Not found error"
      }
    }
  }
}
