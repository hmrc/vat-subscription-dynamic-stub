/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import play.api.libs.json.Json
import play.mvc.Http.Status
import models.DataModel
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import mocks.{MockDataService, MockSchemaValidation}
import testUtils.TestSupport
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl

import scala.concurrent.Future

class SetupDataControllerSpec extends TestSupport with MockSchemaValidation with MockDataService {

  object TestSetupDataController extends SetupDataController(mockSchemaValidation, mockDataService, cc)

  "SetupDataController.addData" when {

    val model: DataModel = DataModel(
      _id = "1234",
      schemaId = "2345",
      method = "GET",
      response = Some(Json.parse("{}")),
      status = Status.OK
    )

    "validateUrlMatch returns 'true'" when {

      "validateResponse returns 'true'" should {

        "return Status OK (200) if data successfully added to stub" in {
          lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))
          lazy val result = TestSetupDataController.addData(request)

          mockValidateUrlMatch(response = true)
          mockValidateResponse(Future.successful(true))
          mockAddEntry(successWriteResult)
          status(result) shouldBe Status.OK
        }

        "return Status InternalServerError (500) if unable to add data to the stub" in {
          lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))
          lazy val result = TestSetupDataController.addData(request)

          mockValidateUrlMatch(response = true)
          mockValidateResponse(Future.successful(true))
          mockAddEntry(errorWriteResult)
          status(result) shouldBe Status.INTERNAL_SERVER_ERROR
        }
      }

      "validateResponse returns 'false'" should {

        "return Status BadRequest (400)" in {
          lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))
          lazy val result = TestSetupDataController.addData(request)

          mockValidateUrlMatch(response = true)
          mockValidateResponse(Future.successful(false))
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }

    "validateUrlMatch returns 'false'" should {

      "return Status BadRequest (400)" in {
        lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))
        lazy val result = TestSetupDataController.addData(request)

        mockValidateUrlMatch(response = false)
        mockLoadUrlRegex(response = "w")
        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "not a GET request" should {

      "return Status BadRequest (400)" in {
        lazy val request = FakeRequest()
          .withBody(Json.toJson(model.copy(method = "DELETE"))).withHeaders(("Content-Type", "application/json"))
        lazy val result = TestSetupDataController.addData(request)

        status(result) shouldBe Status.BAD_REQUEST
      }
    }

    "an exception is thrown by one of the nested functions" should {

      "return Status InternalServerError (500)" in {
        lazy val request = FakeRequest().withBody(Json.toJson(model)).withHeaders(("Content-Type", "application/json"))
        lazy val result = TestSetupDataController.addData(request)

        mockValidateUrlMatch(response = true)
        mockValidateResponse(Future.failed(new Exception("Unexpected exception")))
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }

  "SetupDataController.removeData" should {

    "return Status OK (200) on successful removal of data from the stub" in {
      lazy val request = FakeRequest()
      lazy val result = TestSetupDataController.removeData(RedirectUrl("/validate-relative-path"))(request)

      mockRemoveById(successDeleteResult)

      status(result) shouldBe Status.OK
    }

    "return Status InternalServerError (500) on unsuccessful removal of data from the stub" in {
      lazy val request = FakeRequest()
      lazy val result = TestSetupDataController.removeData(RedirectUrl("/validate-relative-path"))(request)

      mockRemoveById(errorDeleteResult)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "SetupDataController.removeAllData" should {

    "return Status OK (200) on successful removal of all stubbed data" in {
      lazy val request = FakeRequest()
      lazy val result = TestSetupDataController.removeAll()(request)

      mockRemoveAll(successDeleteResult)

      status(result) shouldBe Status.OK
    }

    "return Status InternalServerError (500) on successful removal of all stubbed data" in {
      lazy val request = FakeRequest()
      lazy val result = TestSetupDataController.removeAll()(request)

      mockRemoveAll(errorDeleteResult)

      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }
}
