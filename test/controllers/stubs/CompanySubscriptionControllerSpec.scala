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
import helpers.CgtRefHelper
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{CgtRepository, RouteExceptionRepository, SubscriptionRepository}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import utils.SchemaValidation

import scala.concurrent.Future

class CompanySubscriptionIndividualControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  val companyAddressModel = CompanyAddressModel(
    Some("Line one"),
    Some("Line two"),
    Some("Line three"),
    Some("Line four"),
    Some("Country"),
    Some("Postcode")
  )
  val companySubmissionModel = CompanySubmissionModel(companyAddressModel)

  def setupController(findLatestVersionResult: List[SubscriberModel],
                      ref: String,
                      expectedExceptionCode: Option[Int] = None,
                      isValidJson: Boolean = true): CompanyIndividualSubscriptionController = {

    val mockCollection = mock[CgtRepository[SubscriberModel, String]]
    val mockRepository = mock[SubscriptionRepository]
    val mockCgtRefHelper = mock[CgtRefHelper]
    val mockExceptionsCollection = mock[CgtRepository[RouteExceptionModel, RouteExceptionKeyModel]]

    val mockExceptionsRepository = mock[RouteExceptionRepository]
    val mockSchemaValidation = mock[SchemaValidation]
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
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockCgtRefHelper.generateCGTReference())
      .thenReturn(ref)

    when(mockSchemaValidation.validateJson(anyString(), any[JsValue]())).thenReturn(Future.successful(isValidJson))


    new CompanyIndividualSubscriptionController(mockRepository, mockCgtRefHelper, exceptionTriggersActions, mockSchemaValidation)
  }

  "Calling .returnSubscriptionReference" when {

    "there is an entry in the database for the supplied sap already" should {

      lazy val controller = setupController(List(SubscriberModel("123456789ABCDEF", "CGT123456")), "CGT123456")
      lazy val result = controller.returnSubscriptionReference("123456789ABCDEF")

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "subscriptionCGT" \ "referenceNumber").as[String] shouldBe "CGT123456"
      }
    }

    "there is no entry in the database for the supplied sap already" should {

      val controller = setupController(Nil, "CGT654321")
      lazy val result = controller.returnSubscriptionReference("123456789ABCDEF")

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "subscriptionCGT" \ "referenceNumber").as[String] shouldBe "CGT654321"
      }
    }

    "an invalid payload is sent due to insufficient length of SAP" should {
      lazy val controller = setupController(List(SubscriberModel("123456789ABCDEF", "CGT123456")), "CGT123456", isValidJson = false)
      lazy val result = await(controller.subscribe("123456789ABC")(FakeRequest("POST", "").withJsonBody(Json.toJson(companySubmissionModel))))

      "return a status of 400" in {
        status(result) shouldBe 400
      }
    }
  }
}
