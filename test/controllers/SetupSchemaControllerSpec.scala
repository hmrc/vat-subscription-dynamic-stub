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

package controllers

import mocks.MockSchemaRepository
import models.SchemaModel
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import testUtils.TestSupport

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
      lazy val result = TestSetupSchemaController.addSchema(request)

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

      "Return a status 400 (BadRequest)" in {
        setupMockAddSchema(errorModel)(successWriteResult)
        status(result) shouldBe Status.BAD_REQUEST
      }
    }
  }
}
