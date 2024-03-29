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

import mocks.{MockDataService, MockSchemaValidation}
import models.{DataModel, SchemaModel}
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.mvc.Http.Status
import testUtils.TestSupport
import play.api.test.Helpers.{contentAsString, defaultAwaitTimeout, status}


class RequestHandlerControllerSpec extends TestSupport with MockSchemaValidation with MockDataService {

  object TestRequestHandlerController extends RequestHandlerController(mockSchemaValidation, mockDataService, cc)

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

  lazy val postSuccessRequestSchema = SchemaModel(
    _id = "testRequest",
    url = "someURL",
    method = "POST",
    responseSchema = Json.parse("""{"response" : "sup"}"""),
    requestSchema = Some(Json.parse("""{"request" : "jaffa cakes"}"""))
  )

  lazy val putSuccessRequestSchema = SchemaModel(
    _id = "testRequest",
    url = "someURL",
    method = "PUT",
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
        contentAsString(result) shouldBe s"${successWithBodyModel.response.get}"
      }

      "return a 404 status when the endpoint cannot be found" in {
        lazy val result = TestRequestHandlerController.getRequestHandler("/test")(FakeRequest())

        mockFind(List())
        status(result) shouldBe Status.NOT_FOUND
      }
}

  "The postRequestHandler method" should {

    "return the corresponding response of an incoming POST request" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      mockValidateRequestJson(response = true)

      contentAsString(result) shouldBe s"${successWithBodyModel.response.get}"
    }

    "return a response status when there is no stubbed response body for an incoming POST request" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List(successModel))
      mockValidateRequestJson(response = true)

      status(result) shouldBe Status.OK
    }

    "return a 400 status if the request body doesn't validate against the stub" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      mockValidateRequestJson(response = false)

      status(result) shouldBe Status.BAD_REQUEST
      contentAsString(result) shouldBe Json.obj(
        "code" -> "400",
        "reason" -> "Request did not validate against schema"
      ).toString
    }

    "return a 404 status if the endpoint specified in the POST request can't be found" in {
      lazy val result = TestRequestHandlerController.postRequestHandler("/test")(FakeRequest())

      mockFind(List())

      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe Json.obj(
        "code" -> "NOT_FOUND",
        "reason" -> "No data exists for this request."
      ).toString
    }
  }


  "The putRequestHandler method" should {

    "return the corresponding response of an incoming PUT request" in {
      lazy val result = TestRequestHandlerController.putRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      mockValidateRequestJson(response = true)

      contentAsString(result) shouldBe s"${successWithBodyModel.response.get}"
    }

    "return a response status when there is no stubbed response body for an incoming PUT request" in {
      lazy val result = TestRequestHandlerController.putRequestHandler("/test")(FakeRequest())

      mockFind(List(successModel))
      mockValidateRequestJson(response = true)

      status(result) shouldBe Status.OK
    }

    "return a 400 status if the request body doesn't validate against the stub" in {
      lazy val result = TestRequestHandlerController.putRequestHandler("/test")(FakeRequest())

      mockFind(List(successWithBodyModel))
      mockValidateRequestJson(response = false)

      status(result) shouldBe Status.BAD_REQUEST
      contentAsString(result) shouldBe Json.obj(
        "code" -> "400",
        "reason" -> "Request did not validate against schema"
      ).toString
    }

    "return a 404 status if the endpoint specified in the PUT request can't be found" in {
      lazy val result = TestRequestHandlerController.putRequestHandler("/test")(FakeRequest())

      mockFind(List())

      status(result) shouldBe Status.NOT_FOUND
      contentAsString(result) shouldBe Json.obj(
        "code" -> "NOT_FOUND",
        "reason" -> "No data exists for this request."
      ).toString
    }
  }

}
