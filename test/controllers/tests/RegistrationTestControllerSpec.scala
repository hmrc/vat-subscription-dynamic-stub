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

import models.BusinessPartner
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import repository.{BPMongoConnector, CGTMongoRepository}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class RegistrationTestControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  lazy val controller = {
    val mockRepository = mock[CGTMongoRepository[BusinessPartner, Nino]]
    val mockConnector = mock[BPMongoConnector]

    when(mockConnector.apply())
      .thenReturn(mockRepository)

    when(mockRepository.addEntry(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful())

    when(mockRepository.removeBy(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful())

    new RegistrationTestController(mockConnector)
  }

  "Calling .addRegistrationRecord" should {

    "return a status of 200 with valid Json" in {
      lazy val result = controller.addRegistrationRecord(FakeRequest("POST", "")
        .withJsonBody(Json.toJson(BusinessPartner(Nino("AA123456A"), "CGT123456"))))

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
