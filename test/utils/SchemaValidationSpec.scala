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

package utils

import com.github.fge.jsonschema.main.JsonSchema
import mocks.MockSchemaService
import play.api.libs.json.{JsValue, Json}
import testUtils.TestSupport
import testUtils.TestAssets.schemaModel
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import services.DataService
import testUtils.TestAssets.{jsonSchema, validYamlData, validYamlFailureData, yamlSchema}

import scala.concurrent.Future

class SchemaValidationSpec extends TestSupport with MockSchemaService {

    val mockDataService: DataService = mock[DataService]

    val validation = new SchemaValidation(mockSchemaService)

  "Calling .validateResponse" when {

    "there is a matching JSON schema" when {

      "the data matches the schema" should {

        "return 'true'" in {
          val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")
          mockFindById(Future.successful(schemaModel))
          val result = validation.validateResponse("testSchema", Some(json))
          await(result) shouldBe true
        }
      }

      "the data does not match the schema" should {

        "return 'false'" in {
          val json = Json.parse("""{ "firstName" : "Bob" }""")
          mockFindById(Future.successful(schemaModel))
          val result = validation.validateResponse("testSchema", Some(json))
          await(result) shouldBe false
        }
      }
    }

    "there is a matching YAML schema" when {

      "the data matches the schema" should {

        "return 'true' when matching on the successResponse definition" in {
          mockFindById(Future.successful(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema("successResponse")), schemaType = Some("yaml"))))
          val result = validation.validateResponse("testSchema", Some(validYamlData))
          await(result) shouldBe true
        }

        "return 'true' when matching on the responseSchema definition" in {
          mockFindById(Future.successful(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema("responseSchema")), schemaType = Some("yaml"))))
          val result = validation.validateResponse("testSchema", Some(validYamlData))
          await(result) shouldBe true
        }

        "return 'true' when matching on the SuccessResponseSchema definition" in {
          mockFindById(Future.successful(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema("successResponseSchema")), schemaType = Some("yaml"))))
          val result = validation.validateResponse("testSchema", Some(validYamlData))
          await(result) shouldBe true
        }


        "return 'true' when matching on the failureResponse definition" in {
          mockFindById(Future.successful(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema("successResponse")), schemaType = Some("yaml"))))
          val result = validation.validateResponse("testSchema", Some(validYamlFailureData))
          await(result) shouldBe true
        }
      }

      "the data does not match the schema" should {

        "return 'false'" in {
          val json = Json.parse("""{ "firstName" : "Bob" }""")
          mockFindById(Future.successful(schemaModel.copy(responseSchema = Json.obj("value" -> yamlSchema("successResponse")), schemaType = Some("yaml"))))
          val result = validation.validateResponse("testSchema", Some(json))
          await(result) shouldBe false
        }
      }
    }

    "there is no json body" should {

      "return true" in {
        val result = validation.validateResponse("testSchema", None)
        await(result) shouldBe true
      }
    }
  }

  "Calling .loadUrlRegex" should {

    "return the url of the SchemaModel" in {
      mockFindById(Future.successful(schemaModel))
      lazy val result = validation.loadUrlRegex("testSchema")
      await(result) shouldEqual "/test"
    }
  }

  "Calling .validateUrlMatch" should {
    "return 'true' if the urls match" in {
      mockFindById(Future.successful(schemaModel))
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
      val result = validation.loadRequestSchema(postSchema)
      result.isInstanceOf[JsonSchema]
    }
  }

  "Calling .validateRequestJson" when {

    val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")

    "there is a valid json body" when {

      "a request schema does not exist" should {

        "return true" in {
          mockFindById(Future.successful(schemaModel))
          val result = validation.validateRequestJson("testSchema", Some(json))
          await(result) shouldEqual true
        }
      }

      "a json value is not passed" should {

        "return true" in {
          mockFindById(Future.successful(schemaModel.copy("testSchema", "/test", "GET", responseSchema = jsonSchema, requestSchema =  Some(postSchema))))
          val result = validation.validateRequestJson("testSchema", None)
          await(result) shouldBe true
        }
      }

      "a request schema exists" should {

        "return true" in {
          mockFindById(Future.successful(schemaModel.copy("testSchema", "/test", "GET", responseSchema = jsonSchema, requestSchema =  Some(postSchema))))
          val result = validation.validateRequestJson("testSchema", Some(json))
          await(result) shouldBe true
        }
      }
    }

    "there is an invalid json body" should {

      "return false" in {
        val invalidJson = Json.parse("""{ "firstName" : "Bob" }""")
        mockFindById(Future.successful(schemaModel.copy("testSchema","/test","GET", responseSchema = jsonSchema, requestSchema = Some(postSchema))))
        lazy val result = validation.validateRequestJson("testSchema", Some(invalidJson))
        await(result) shouldEqual false
      }
    }

    "there is no json body" should {

      "return true" in {
        mockFindById(Future.successful(schemaModel))
        val result = validation.validateRequestJson("testSchema", None)
        await(result) shouldBe true
      }
    }

    "an unexpected exception was thrown retrieving the schema" should {

      "throw an exception containing the error message" in {
        mockFindById(Future.failed(new Exception("oops")))
        val ex = intercept[Exception](await(validation.validateRequestJson("testSchema", Some(json))))
        ex.getMessage shouldEqual "Schema could not be retrieved due to exception: oops"
      }
    }
  }
}
