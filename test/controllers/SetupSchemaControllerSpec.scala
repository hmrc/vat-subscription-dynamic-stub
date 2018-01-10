/*
 * Copyright 2018 HM Revenue & Customs
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

import mocks.MockSchemaRepository
import models.SchemaModel
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import testUtils.TestSupport

import scala.concurrent.Future

class SetupSchemaControllerSpec extends TestSupport with MockSchemaRepository {

  object TestSetupSchemaController extends SetupSchemaController(mockSchemaRepository)

  "The SetupSchemaController" when {

    "a request to add a valid schema is successful" should {

      lazy val successModel = SchemaModel(
        _id = "test",
        url = "/test",
        method = "GET",
        responseSchema = Json.parse("{}")
      )
      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result: Future[Result] = TestSetupSchemaController.addSchema(request)

      "Return a status 200 (OK)" in {
        setupMockAddSchema(successModel)(successWriteResult)
        status(result) shouldBe Status.OK
      }

      s"Result Body 'Successfully added Schema: ${Json.toJson(successModel)}'" in {
        setupMockAddSchema(successModel)(successWriteResult)
        await(bodyOf(result)) shouldBe s"Successfully added Schema: ${Json.toJson(successModel)}"
      }
    }

    "a request to add a valid schema is unsuccessful" should {

      lazy val successModel = SchemaModel(
        _id = "test",
        url = "/test",
        method = "GET",
        responseSchema = Json.parse("{}")
      )

      lazy val errorModel = SchemaModel(
        _id = "test",
        url = "/test",
        method = "GET",
        responseSchema = Json.parse("{}")
      )

      lazy val request = FakeRequest().withBody(Json.toJson(successModel)).withHeaders(("Content-Type","application/json"))
      lazy val result = TestSetupSchemaController.addSchema(request)

      "Return a status 500 (ISE)" in {
        setupMockAddSchema(successModel)(errorWriteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }

      s"Result Body 'Could not store data'" in {
        setupMockAddSchema(successModel)(errorWriteResult)
        await(bodyOf(result)) shouldBe "Could not store data"
      }

//      "Return a status 400 (BadRequest)" in {
//        lazy val request1 = FakeRequest().withBody(Json.toJson(errorModel)).withHeaders(("Content-Type","application/json"))
//        lazy val result1 = TestSetupSchemaController.addSchema(request1)
//        setupMockAddSchema(errorModel)(successWriteResult)
//        status(result1) shouldBe Status.BAD_REQUEST
//      }
    }

    "removing a schema is successful" should {
      "Return a status 200 (OK)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeSchema("someId")(request)

        setupMockRemoveSchema("someId")(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "removing a schema is unsuccessful" should {
      "Return a status 500 (ISE)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeSchema("someId")(request)

        setupMockRemoveSchema("someId")(errorWriteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }

    "removing all schemas is successful" should {
      "Return a status 200 (OK)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeAll()(request)

        setupMockRemoveAllSchemas(successWriteResult)
        status(result) shouldBe Status.OK
      }
    }

    "removing all schemas is unsuccessful" should {
      "Return a status 500 (ISE)" in {
        lazy val request = FakeRequest()
        lazy val result = TestSetupSchemaController.removeAll()(request)

        setupMockRemoveAllSchemas(errorWriteResult)
        status(result) shouldBe Status.INTERNAL_SERVER_ERROR
      }
    }
  }
}
