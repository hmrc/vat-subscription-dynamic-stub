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

import actions.NinoExceptionTriggersActions
import helpers.SAPHelper
import models.{BusinessPartner, RegisterModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repository.{BPMongoConnector, CGTMongoRepository}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class RegistrationControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(findLatestVersionResult: Future[List[BusinessPartner]], addEntryResult: Future[Unit], sap: String) = {

    val mockRepository = mock[CGTMongoRepository[BusinessPartner, Nino]]
    val mockConnector = mock[BPMongoConnector]
    val mockSAPHelper = mock[SAPHelper]
    def exceptionTriggersActions() = fakeApplication.injector.instanceOf[NinoExceptionTriggersActions]

    when(mockConnector.repository)
      .thenReturn(mockRepository)

    when(mockRepository.addEntry(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(addEntryResult)

    when(mockRepository.findLatestVersionBy(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockSAPHelper.generateSap())
      .thenReturn(sap)

    new RegistrationController(mockConnector, mockSAPHelper, exceptionTriggersActions())
  }

  "Calling registerBusinessPartner" when {

    "a list with business partners is returned" should {
      val controller = setupController(Future.successful(List(BusinessPartner(Nino("AA123456A"), "123456789"))),
        Future.successful(()), "")
      lazy val result = controller.registerBusinessPartner("AA123456A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA123456A")))))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid SAP" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "123456789"
      }
    }

    "a list with no Business partners is returned" should {
      val controller = setupController(Future.successful(List()),
        Future.successful(()), "987654321")
      lazy val result = controller.registerBusinessPartner("AA123456A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA123456A")))))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid SAP" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "987654321"
      }
    }

    "passing in a nino for an error scenario" should {
      val controller = setupController(Future.successful(List(BusinessPartner(Nino("AA123456A"), "CGT123456"))),
        Future.successful(()), "CGT654321")
      lazy val result = controller.registerBusinessPartner("AA404404A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA404404A")))))

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
