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

import mocks.MockSchemaService
import models.SchemaModel
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import testUtils.TestSupport
import play.api.http.Status
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}

import scala.concurrent.Future

class SetupSchemaControllerSpec extends TestSupport with MockSchemaService {

  object TestSetupSchemaController extends SetupSchemaController(mockSchemaService, cc)

  val successModel: SchemaModel = SchemaModel(
    _id = "test",
    url = "/test",
    method = "GET",
    responseSchema = Json.parse("{}")
  )

  "The SetupSchemaController" when {

    "a request to add a valid schema is successful" should {

      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result: Future[Result] = {
        mockAddEntry(Future(successWriteResult))
        TestSetupSchemaController.addSchema(request)
      }

      "return a status 200 (OK)" in {
        status(result) shouldBe Status.OK
      }

      "return a response body that contains a confirmation" in {
        contentAsString(result) should include("Successfully added Schema")
      }
    }

    "a request to add a valid schema is unsuccessful due to a mongo write result error" should {

      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result = {
        mockAddEntry(Future(errorWriteResult))
        TestSetupSchemaController.addSchema(request)
      }

      "return a status 500 (INTERNAL_SERVER_ERROR)" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return a response body of 'Could not store data'" in {
        contentAsString(result) shouldBe "Could not store data"
      }
    }

    "a request to add a valid schema is unsuccessful due to an unexpected exception" should {

      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result = {
        mockAddEntry(Future.failed(new Exception("something went wrong")))
        TestSetupSchemaController.addSchema(request)
      }

      "return a status 500 (INTERNAL_SERVER_ERROR)" in {
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      "return a response body of 'Schema could not be added due to exception:' with the exception message" in {
        contentAsString(result) shouldBe "Schema could not be added due to exception: something went wrong"
      }
    }

    "invalid JSON is sent in the request to add a schema" should {

      val json = Json.obj("_id" -> "test", "url" -> "test", "method" -> 1, "responseSchema" -> Json.parse("{}"))
      lazy val request = FakeRequest().withBody(json).withHeaders(("Content-Type","application/json"))
      lazy val result: Future[Result] = TestSetupSchemaController.addSchema(request)

      "return a status 400 (BAD_REQUEST)" in {
        status(result) shouldBe Status.BAD_REQUEST
      }

      "return an error message containing details about the fields that are invalid" in {
        contentAsString(result) shouldBe "Invalid SchemaModel payload: " +
          "List((/method,List(JsonValidationError(List(error.expected.jsstring),List()))))"
      }
    }

    "removing a schema is successful" should {

      "return a status 200 (OK)" in {
        lazy val result = TestSetupSchemaController.removeSchema("someId")(FakeRequest())

        mockRemoveById(successDeleteResult)
        status(result) shouldBe Status.OK
      }
    }

    "removing a schema is unsuccessful" should {

      "return a status 500 (ISE)" in {
        lazy val result = TestSetupSchemaController.removeSchema("someId")(FakeRequest())

        mockRemoveById(errorDeleteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "removing all schemas is successful" should {

      "return a status 200 (OK)" in {
        lazy val result = TestSetupSchemaController.removeAll()(FakeRequest())

        mockRemoveAll(successDeleteResult)
        status(result) shouldBe Status.OK
      }
    }

    "removing all schemas is unsuccessful" should {

      "return a status 500 (ISE)" in {
        lazy val result = TestSetupSchemaController.removeAll()(FakeRequest())

        mockRemoveAll(errorDeleteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
