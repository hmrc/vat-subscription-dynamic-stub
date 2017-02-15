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

import actions.FullDetailsExceptionTriggersActions
import javax.inject.Singleton
import helpers.SapHelper
import models.{FullDetailsModel, NonResidentBusinessPartnerModel}
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{CgtRepository, NonResidentBusinessPartnerRepository}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

@Singleton
class GhostRegistrationControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(findLatestVersionResult: Future[List[NonResidentBusinessPartnerModel]], addEntryResult: Future[Unit], sap: String): GhostRegistrationController = {

    val mockCollection = mock[CgtRepository[NonResidentBusinessPartnerModel, FullDetailsModel]]
    val mockRepository = mock[NonResidentBusinessPartnerRepository]
    val mockSAPHelper = mock[SapHelper]

    def exceptionTriggersActions() = fakeApplication.injector.instanceOf[FullDetailsExceptionTriggersActions]

    when(mockRepository.apply())
      .thenReturn(mockCollection)

    when(mockCollection.addEntry(any())(any()))
      .thenReturn(addEntryResult)

    when(mockCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockSAPHelper.generateSap())
      .thenReturn(sap)

    new GhostRegistrationController(mockRepository, mockSAPHelper, exceptionTriggersActions())
  }

  "Calling registerGhostBusinessPartner" when {

    "a list with existing business partners is returned" should {

      val fullDetailsModel = FullDetailsModel("Daniel", "Dorito", "25 Big House", None, "New York", None, "NY1 1NY", "United States of America")
      val controller = setupController(Future.successful(List(NonResidentBusinessPartnerModel(fullDetailsModel, "123456789"))),
        Future.successful({}), "")
      lazy val result = controller.registerBusinessPartner()(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(fullDetailsModel)))

      "return a status of 200" in {
        status(result) shouldBe 200
      }
    }

    "a list with no existing business partners is returned" should {
      val fullDetailsModel = FullDetailsModel("Michael", "Dorito", "25 Big House", None, "New York", None, "NY1 1NY", "United States of America")
      val controller = setupController(Future.successful(List()), Future.successful({}), "1234567890")
      lazy val result = controller.registerBusinessPartner()(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(fullDetailsModel)))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid SAP" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        json.as[String] shouldBe "1234567890"
      }
    }

    "passing in a full details model for an error scenario" should {
      val fullDetailsModel = FullDetailsModel("John", "Smith", "25 Big House", None, "Telford", None, "ABC 404", "UK")
      val controller = setupController(Future.successful(List(NonResidentBusinessPartnerModel(fullDetailsModel, "1234567890"))), Future.successful({}), "1234567890")
      lazy val result = controller.registerBusinessPartner()(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(fullDetailsModel)))

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
