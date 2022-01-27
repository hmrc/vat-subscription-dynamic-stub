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
import testUtils.TestAssets.{jsonSchema, validYamlData, yamlSchema}

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

  val schemaModel: SchemaModel = SchemaModel("testSchema", "/test", "GET", responseSchema = jsonSchema)

  "Calling .validateResponse" when {

    "there is a matching JSON schema" when {

      "the data matches the schema" should {

        "return 'true'" in {
          val validation = setupMocks(schemaModel)
          val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")
          val result = validation.validateResponse("testSchema", Some(json))
          await(result) shouldBe true
        }
      }

      "the data does not match the schema" should {

        "return 'false'" in {
          val validation = setupMocks(schemaModel)
          val json = Json.parse("""{ "firstName" : "Bob" }""")
          val result = validation.validateResponse("testSchema", Some(json))
          await(result) shouldBe false
        }
      }
    }

    "there is a matching YAML schema" when {

      "the data matches the schema" should {

        "return 'true'" in {
          val validation =
            setupMocks(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema), schemaType = Some("yaml")))
          val result = validation.validateResponse("testSchema", Some(validYamlData))
          await(result) shouldBe true
        }
      }

      "the data does not match the schema" should {

        "return 'false'" in {
          val validation =
            setupMocks(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema), schemaType = Some("yaml")))
          val json = Json.parse("""{ "firstName" : "Bob" }""")
          val result = validation.validateResponse("testSchema", Some(json))
          await(result) shouldBe false
        }
      }
    }

    "there is no matching schema" should {

      "throw a null pointer exception" in {
        val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema))
        intercept[NullPointerException](validation.validateResponse("madeUpSchema", Some(Json.obj("a" -> "b"))))
      }
    }

    "there is no json body" should {

      "return true" in {
        val validation = setupMocks(schemaModel)
        val result = validation.validateResponse("testSchema", None)
        await(result) shouldBe true
      }
    }
  }

  "Calling .loadUrlRegex" should {
    lazy val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema))

    "return the url of the SchemaModel" in {
      lazy val result = validation.loadUrlRegex("testSchema")
      await(result) shouldEqual "/test"
    }
  }

  "Calling .validateUrlMatch" should {
    lazy val validation = setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema))
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
        setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema, requestSchema = Some(postSchema)))
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
            setupMocks(SchemaModel("testSchema", "/test", "GET", responseSchema = jsonSchema, requestSchema = Some(postSchema)))
          val result = validation.validateRequestJson("testSchema", Some(json))
          await(result) shouldEqual true
        }
      }

      "a request schema does not exist" should {

        "return true" in {
          val validation =
            setupMocks(SchemaModel("testSchema", "/test", "GET", responseSchema = jsonSchema, requestSchema = None))
          val result = validation.validateRequestJson("testSchema", Some(json))
          await(result) shouldBe true
        }
      }
    }

    "there is an invalid json body" should {

      lazy val validation =
        setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema, requestSchema = Some(postSchema)))
      val invalidJson = Json.parse("""{ "firstName" : "Bob" }""")

      lazy val result = validation.validateRequestJson("testSchema", Some(invalidJson))

      "return false" in {
        await(result) shouldEqual false
      }
    }

    "there is no json body" should {

      "return true" in {
        val validation =
          setupMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema, requestSchema = Some(postSchema)))
        val result = validation.validateRequestJson("testSchema", None)
        await(result) shouldBe true
      }
    }

    "an unexpected exception was thrown retrieving the schema" should {

      "throw an exception containing the error message" in {
        val validation = setupFutureFailedMocks(SchemaModel("testSchema","/test","GET", responseSchema = jsonSchema))
        val ex = intercept[Exception](await(validation.validateRequestJson("testSchema", Some(json))))
        ex.getMessage shouldEqual "Schema could not be retrieved due to exception: oops"
      }
    }
  }
}
