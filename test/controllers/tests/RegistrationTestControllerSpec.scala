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

package controllers.tests

import models.BusinessPartnerModel
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import repositories.{BusinessPartnerRepository, CgtRepository}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class RegistrationTestControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  lazy val controller: RegistrationTestController = {
    val mockCollection = mock[CgtRepository[BusinessPartnerModel, Nino]]
    val mockConnection = mock[BusinessPartnerRepository]

    when(mockConnection.apply())
      .thenReturn(mockCollection)

    when(mockCollection.addEntry(any())(any()))
      .thenReturn(Future.successful({}))

    when(mockCollection.removeBy(any())(any()))
      .thenReturn(Future.successful({}))

    new RegistrationTestController(mockConnection)
  }

  "Calling .addRegistrationRecord" should {

    "return a status of 200 with valid Json" in {
      lazy val result = controller.addRegistrationRecord(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(BusinessPartnerModel(Nino("AA123456A"), "CGT123456"))))

      status(result) shouldBe 200
    }

    "return a status of 400 with invalid Json" in {
      lazy val result = controller.addRegistrationRecord(FakeRequest("POST", "")
        .withJsonBody(Json.toJson("Invalid data")))

      status(result) shouldBe 400
    }
  }

  "Calling .removeRegistrationRecord" should {

    "return a status of 200 with valid Json" in {
      lazy val result = controller.removeRegistrationRecord(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(Nino("AA123456A"))))

      status(result) shouldBe 200
    }

    "return a status of 400 with invalid Json" in {
      lazy val result = controller.removeRegistrationRecord(FakeRequest("POST", "")
        .withJsonBody(Json.toJson("Invalid data")))

      status(result) shouldBe 400
    }
  }

}
