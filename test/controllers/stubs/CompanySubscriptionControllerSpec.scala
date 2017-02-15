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

import helpers.CgtRefHelper
import models.{CompanyAddressModel, CompanySubmissionModel, SubscriberModel}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.mvc.Results
import play.api.test.Helpers._
import repositories.{CgtRepository, SubscriptionRepository}
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

  def setupController(findLatestVersionResult: Future[List[SubscriberModel]], addEntryResult: Future[Unit], ref: String): CompanySubscriptionController = {

    val mockCollection = mock[CgtRepository[SubscriberModel, String]]
    val mockRepository = mock[SubscriptionRepository]
    val mockCgtRefHelper = mock[CgtRefHelper]

    when(mockRepository.apply())
      .thenReturn(mockCollection)

    when(mockCollection.addEntry(any())(any()))
      .thenReturn(addEntryResult)

    when(mockCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockCgtRefHelper.generateCGTReference())
      .thenReturn(ref)

    new CompanySubscriptionController(mockRepository, mockCgtRefHelper)
  }

  "Calling .validateBody" when {

    "the model has a single empty sap field should return false" in {
      companySubscriptionController.validateBody(noSapCompanySubmissionModel) shouldEqual false
    }

    "the model has a single empty registered address field should return false" in {
      companySubscriptionController.validateBody(noRegAddressCompanySubmissionModel) shouldEqual false
    }

    "the model has a single empty sap field should return false" in {
      companySubscriptionController.validateBody(noCorAddressCompanySubmissionModel) shouldEqual false
    }

    "the model has all fields supplied should return true" in {
      companySubscriptionController.validateBody(companySubmissionModel) shouldEqual true
    }
  }

  "Calling .exceptionTriggers" when {

    "the supplied sap is 003404404 should return" in {
      companySubscriptionController.checkExceptionTriggers("003404404") shouldEqual Some(Results.NotFound(Json.toJson("Not found error")))
    }

    "the supplied sap is 003400400 should return" in {
      companySubscriptionController.checkExceptionTriggers("003400400") shouldEqual Some(Results.BadRequest(Json.toJson("Bad request error")))
    }

    "the supplied sap is 003500500 should return" in {
      companySubscriptionController.checkExceptionTriggers("003500500") shouldEqual Some(Results.BadGateway(Json.toJson("Bad gateway error")))
    }

    "the supplied sap is 003500500 should return" in {
      companySubscriptionController.checkExceptionTriggers("003502502") shouldEqual Some(Results.InternalServerError(Json.toJson("Internal server error")))
    }

    "the supplied sap is 003500500 should return" in {
      companySubscriptionController.checkExceptionTriggers("003503503") shouldEqual Some(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
    }

    "the supplied sap is 003500500 should return" in {
      companySubscriptionController.checkExceptionTriggers("003408408") shouldEqual Some(Results.RequestTimeout(Json.toJson("Timeout error")))
    }

    "the supplied sap is anything else" should {

      //TODO: This is a perfect place to refactor to use the multiple test suite (Scala check)
      "return true when called with 192381201" in {
        companySubscriptionController.checkExceptionTriggers("192381201") shouldEqual None
      }

      "return true when called with 127514920" in {
        companySubscriptionController.checkExceptionTriggers("127514920") shouldEqual None
      }

      "return true when called with 756293710" in {
        companySubscriptionController.checkExceptionTriggers("756293710") shouldEqual None
      }
    }
  }

  "Calling .returnSubscriptionReference" when {

    "there is an entry in the database for the supplied sap already" should {

      val controller = setupController(Future.successful(List(SubscriberModel("123456789", "CGT123456"))), Future.successful({}), "CGT123456")
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

      val controller = setupController(Future.successful(List()), Future.successful({}), "CGT654321")
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

  "Calling .returnBody" when {

    "the body does not have all parameters defined" should {

      val result = companySubscriptionController.returnBody(noSapCompanySubmissionModel)

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "Body of request did not contain the expected values for the company submission model"
      }
    }
  }
}
