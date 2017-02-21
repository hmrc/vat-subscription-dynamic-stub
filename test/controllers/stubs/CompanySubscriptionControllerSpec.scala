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

import javax.inject.Inject

import actions.ExceptionTriggersActions
import helpers.CgtRefHelper
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.Helpers._
import repositories.{CgtRepository, RouteExceptionRepository, SubscriptionRepository}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class CompanySubscriptionControllerSpec @Inject()(companySubscriptionController: CompanySubscriptionController)
  extends UnitSpec with MockitoSugar with WithFakeApplication {

  val companyAddressModel = CompanyAddressModel(
    Some("Line one"),
    Some("Line two"),
    Some("Line three"),
    Some("Line four"),
    Some("Country"),
    Some("Postcode")
  )
  val noSapCompanySubmissionModel = CompanySubmissionModel(None, Some(companyAddressModel), Some(companyAddressModel))
  val noRegAddressCompanySubmissionModel = CompanySubmissionModel(Some("dummySap"), None, Some(companyAddressModel))
  val noCorAddressCompanySubmissionModel = CompanySubmissionModel(Some("dummySap"), Some(companyAddressModel), None)
  val companySubmissionModel = CompanySubmissionModel(Some("dummySap"), Some(companyAddressModel), Some(companyAddressModel))

  def setupController(findLatestVersionResult: List[SubscriberModel],
                      ref: String,
                      expectedExceptionCode: Option[Int] = None): CompanySubscriptionController = {

    val mockCollection = mock[CgtRepository[SubscriberModel, String]]
    val mockRepository = mock[SubscriptionRepository]
    val mockCgtRefHelper = mock[CgtRefHelper]
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
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockCgtRefHelper.generateCGTReference())
      .thenReturn(ref)

    new CompanySubscriptionController(mockRepository, mockCgtRefHelper, exceptionTriggersActions)
  }

  "Calling .returnSubscriptionReference" when {

    "there is an entry in the database for the supplied sap already" should {

      val controller = setupController(List(SubscriberModel("123456789", "CGT123456")), "CGT123456")
      lazy val result = controller.returnSubscriptionReference("123456789")

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

    "there is no entry in the database for the supplied sap already" should {

      val controller = setupController(Nil, "CGT654321")
      lazy val result = controller.returnSubscriptionReference("123456789")

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
  }
}
