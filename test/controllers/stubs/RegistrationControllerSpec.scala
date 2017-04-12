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

import actions.ExceptionTriggersActions
import helpers.SapHelper
import models.{BusinessPartnerModel, RegisterModel, RouteExceptionKeyModel, RouteExceptionModel}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{BusinessPartnerRepository, CgtRepository, RouteExceptionRepository}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class RegistrationControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(existingBusinessPartners: List[BusinessPartnerModel],
                      sap: String,
                      expectedExceptionCode: Option[Int] = None): RegistrationController = {

    val mockCollection = mock[CgtRepository[BusinessPartnerModel, Nino]]
    val mockRepository = mock[BusinessPartnerRepository]
    val mockSAPHelper = mock[SapHelper]
    val mockExceptionsCollection = mock[CgtRepository[RouteExceptionModel, RouteExceptionKeyModel]]
    val mockExceptionsRepository = mock[RouteExceptionRepository]
    val exceptionTriggersActions = new ExceptionTriggersActions(mockExceptionsRepository)
    val expectedException = expectedExceptionCode.fold(List[RouteExceptionModel]()) {
      code => List(RouteExceptionModel("", "", code))
    }

    when(mockExceptionsRepository.apply())
      .thenReturn(mockExceptionsCollection)

    when(mockExceptionsCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(expectedException))

    when(mockRepository.apply())
      .thenReturn(mockCollection)

    when(mockCollection.addEntry(any())(any()))
      .thenReturn(Future.successful({}))

    when(mockCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(existingBusinessPartners))

    when(mockSAPHelper.generateSap())
      .thenReturn(sap)

    new RegistrationController(mockRepository, mockSAPHelper, exceptionTriggersActions)
  }

  "Calling registerBusinessPartner" when {

    "the nino has a corresponding business partner" should {
      val controller = setupController(List(BusinessPartnerModel(Nino("AA123456A"), "123456789")), "")
      lazy val result = controller.registerBusinessPartner("AA123456A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA123456A")))))

      "return a status of 409/Conflicted" in {
        status(result) shouldBe 409
      }
    }

    "passing in a nino for an error scenario" should {
      val controller = setupController(List(BusinessPartnerModel(Nino("AA123456A"), "CGT123456")), "CGT654321", Some(NOT_FOUND))
      lazy val result = controller.registerBusinessPartner("AA404404A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA404404A")))))

      "return a status of 404" in {
        status(result) shouldBe 404
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return an error code" in {
        val data = contentAsJson(result)
        data shouldEqual Json.obj("code" -> "404", "reason" -> "Not found error")
      }
    }

    "nino has no corresponding business partners" should {
      val controller = setupController(List.empty, "123456789")
      lazy val result = controller.registerBusinessPartner("AA123456B")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA123456B")))))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a generated sap" in {
        val data = contentAsJson(result)
        data shouldEqual Json.obj("safeId" -> "123456789")
      }
    }
  }

  "Calling obtainDetails" when {
    "supplied with a nino that is associated with a preexisting BP" should {
      val controller = setupController(List(BusinessPartnerModel(Nino("AA123456A"), "123456789")), "")
      lazy val result = controller.getExistingSAP("AA123456A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA123456A")))))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid SAP" in {
        val data = contentAsJson(result)
        data shouldEqual Json.obj("safeId" -> "123456789")
      }
    }

    "supplied with a nino that is associated with a preexisting BP BUT an internal error occurs" should {
      val controller = setupController(List(BusinessPartnerModel(Nino("AA123456A"), "123456789")), "", Some(INTERNAL_SERVER_ERROR))
      lazy val result = controller.getExistingSAP("AA123456A")(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(RegisterModel(Nino("AA123456A")))))

      "return a status of 500" in {
        status(result) shouldBe 500
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return an error code" in {
        val data = contentAsJson(result)
        data shouldEqual Json.obj("code" -> "500", "reason" -> "Internal server error")
      }
    }

    //TODO: Add new test scenario for new error guard (no point retesting an error guard that's soon-to-be inapplicable)
  }
}
