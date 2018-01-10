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

import mocks.{MockDataRepository, MockSchemaValidation}
import models.{DataModel, SchemaModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, Result}
import play.api.test.{FakeHeaders, FakeRequest}
import play.mvc.Http.Status
import testUtils.TestSupport

import scala.concurrent.Future

class RequestHandlerControllerSpec extends TestSupport with MockSchemaValidation with MockDataRepository {

  object TestRequestHandlerController extends RequestHandlerController(mockSchemaValidation, mockDataRepository)

  lazy val successModel = DataModel(
    _id = "test",
    schemaId = "testID1",
    method = "GET",
    status = Status.OK,
    response = None
  )

  lazy val successWithBodyModel = DataModel(
    _id = "test",
    schemaId = "testID2",
    method = "GET",
    status = Status.OK,
    response = Some(Json.parse("""{"something" : "hello"}"""))
  )

  lazy val successRequestSchema = SchemaModel(
    _id = "testRequest",
    url = "someURL",
    method = "POST",
    responseSchema = Json.parse("""{"response" : "sup"}"""),
    requestSchema = Some(Json.parse("""{"request" : "jaffa cakes"}"""))
  )

  "The getRequestHandler method" should {

    "return the status code specified in the model" in {
      lazy val result = TestRequestHandlerController.getRequestHandler("/test")(FakeRequest())

      mockFind(List(successModel))
      status(result) shouldBe Status.OK
    }

    "return the status and body" in {
      lazy val result = TestRequestHandlerController.getRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      status(result) shouldBe Status.OK
      await(bodyOf(result)) shouldBe s"${successWithBodyModel.response.get}"
    }

    "return a 400 status when the endpoint cannot be found" in {
      lazy val result = TestRequestHandlerController.getRequestHandler("/test")(FakeRequest())

      mockFind(List())
      status(result) shouldBe Status.BAD_REQUEST
    }
  }

  "The postRequestHandler method" should {

    "return the corresponding response of an incoming POST request" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      mockValidateRequestJson(successWithBodyModel.schemaId, successRequestSchema.requestSchema)(response = true)

      await(bodyOf(result)) shouldBe s"${successWithBodyModel.response.get}"
    }

    "return a response status when there is no stubbed response body for an incoming POST request" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List(successModel))
      mockValidateRequestJson(successModel.schemaId, successRequestSchema.requestSchema)(response = true)

      status(result) shouldBe Status.OK
    }

    "return a 400 status if the request body doesn't validate against the stub" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      mockValidateRequestJson(successWithBodyModel.schemaId, None)(response = false)

      status(result) shouldBe Status.BAD_REQUEST
      await(bodyOf(result)) shouldBe s"The Json Body:\n\nNone did not validate against the Schema Definition"
    }

    "return a 400 status if the endpoint specified in the POST request can't be found" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List())

      status(result) shouldBe Status.BAD_REQUEST
      await(bodyOf(result)) shouldBe s"Could not find endpoint in Dynamic Stub matching the URI: /"
    }
  }

}
