/*
 * Copyright 2022 HM Revenue & Customs
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

package utils

import com.github.fge.jsonschema.main.JsonSchema
import models.SchemaModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.libs.json.{JsValue, Json}
import repositories.{DynamicStubRepository, SchemaRepository}
import testUtils.TestSupport
import play.api.test.Helpers.{await, defaultAwaitTimeout}

import scala.concurrent.Future

class SchemaValidationSpec extends TestSupport {

  def setupMocks(schemaModel: SchemaModel): SchemaValidation = {
    val mockCollection = mock[DynamicStubRepository[SchemaModel, String]]
    val mockConnection = mock[SchemaRepository]

    when(mockConnection.apply()).thenReturn(mockCollection)

    when(mockCollection.findById(ArgumentMatchers.eq(schemaModel._id))(ArgumentMatchers.any()))
      .thenReturn(Future.successful(schemaModel))

    new SchemaValidation(mockConnection)
  }

  def setupFutureFailedMocks(schemaModel: SchemaModel): SchemaValidation = {
    val mockCollection = mock[DynamicStubRepository[SchemaModel, String]]
    val mockConnection = mock[SchemaRepository]

    when(mockConnection.apply()).thenReturn(mockCollection)

    when(mockCollection.findById(ArgumentMatchers.eq(schemaModel._id))(ArgumentMatchers.any()))
      .thenReturn(Future.failed(new Exception("oops")))

    new SchemaValidation(mockConnection)
  }

  val schema: JsValue = Json.parse("""
    {
      "title": "Person",
      "type": "object",
      "properties": {
        "firstName": {
          "type": "string"
        },
        "lastName": {
          "type": "string"
        }
      },
      "required": ["firstName", "lastName"]
    }"""
  )

  "Calling .loadResponseSchema" when {

    "there is a matching schema in mongo" should {

      "return a json schema" in {
        val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
        val result = validation.loadResponseSchema("testSchema")
        result.isInstanceOf[JsonSchema]
      }
    }

    "there is no matching schema in mongo" should {

      "throw a null pointer exception" in {
        val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
        intercept[NullPointerException](validation.loadResponseSchema("madeUpSchema"))
      }
    }

    "an unexpected exception was thrown retrieving the schema" should {

      "throw an exception containing the error message" in {
        val validation = setupFutureFailedMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
        val ex = intercept[Exception](await(validation.loadResponseSchema("testSchema")))
        ex.getMessage shouldEqual "Schema could not be retrieved due to exception: oops"
      }
    }
  }

  "Calling .validateResponseJson" when {

    "there is a valid json body" should {

      "return true" in {
        val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
        val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")
        val result = validation.validateResponseJson("testSchema", Some(json))
        await(result) shouldEqual true
      }
    }

    "there is an invalid json body" should {

      lazy val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
      val json = Json.parse("""{ "firstName" : "Bob" }""")

      lazy val result = validation.validateResponseJson("testSchema", Some(json))

      "return false" in {
        await(result) shouldEqual false
      }
    }

    "there is no json body" should {

      "return true" in {
        val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
        val result = validation.validateResponseJson("testSchema", None)
        await(result) shouldBe true
      }
    }
  }

  "Calling .loadUrlRegex" should {
    lazy val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))

    "return the url of the SchemaModel" in {
      lazy val result = validation.loadUrlRegex("testSchema")
      await(result) shouldEqual "/test"
    }
  }

  "Calling .validateUrlMatch" should {
    lazy val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
    "return 'true' if the urls match" in {
      lazy val result = validation.validateUrlMatch("testSchema", "/test")
      await(result) shouldEqual true
    }

  }

  val postSchema: JsValue = Json.parse("""
    {
      "title": "Person",
      "type": "object",
      "properties": {
        "firstName": {
          "type": "string"
        },
        "lastName": {
          "type": "string"
        }
      },
      "required": ["firstName", "lastName"]
    }"""
  )

  "Calling .loadRequestSchema" should {

    "return the provided json schema in the expected format" in {
      val validation =
        setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema, requestSchema = Some(postSchema)))
      val result = validation.loadRequestSchema(postSchema)
      result.isInstanceOf[JsonSchema]
    }
  }

  "Calling .validateRequestJson" when {

    val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")

    "there is a valid json body" when {

      "a request schema exists" should {

        "return true" in {
          val validation =
            setupMocks(SchemaModel("testSchema", "/test", "GET", responseSchema = schema, requestSchema = Some(postSchema)))
          val result = validation.validateRequestJson("testSchema", Some(json))
          await(result) shouldEqual true
        }
      }

      "a request schema does not exist" should {

        "return true" in {
          val validation =
            setupMocks(SchemaModel("testSchema", "/test", "GET", responseSchema = schema, requestSchema = None))
          val result = validation.validateRequestJson("testSchema", Some(json))
          await(result) shouldBe true
        }
      }
    }

    "there is an invalid json body" should {

      lazy val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema, requestSchema = Some(postSchema)))
      val invalidJson = Json.parse("""{ "firstName" : "Bob" }""")

      lazy val result = validation.validateRequestJson("testSchema", Some(invalidJson))

      "return false" in {
        await(result) shouldEqual false
      }
    }

    "there is no json body" should {

      "return true" in {
        val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema, requestSchema = Some(postSchema)))
        val result = validation.validateRequestJson("testSchema", None)
        await(result) shouldBe true
      }
    }

    "an unexpected exception was thrown retrieving the schema" should {

      "throw an exception containing the error message" in {
        val validation = setupFutureFailedMocks(SchemaModel("testSchema","/test","GET", responseSchema = schema))
        val ex = intercept[Exception](await(validation.validateRequestJson("testSchema", Some(json))))
        ex.getMessage shouldEqual "Schema could not be retrieved due to exception: oops"
      }
    }
  }
}
