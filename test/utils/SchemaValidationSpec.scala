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

package utils

import com.github.fge.jsonschema.main.JsonSchema
import models.SchemaModel
import org.mockito.ArgumentMatchers._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json.Json
import repositories.{CgtRepository, SchemaRepository}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import utils2.SchemaValidation

import scala.concurrent.Future

class SchemaValidationSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupMocks(schemaModel: List[SchemaModel]): SchemaValidation = {
    val mockCollection = mock[CgtRepository[SchemaModel, String]]
    val mockConnection = mock[SchemaRepository]

    when(mockConnection.apply())
      .thenReturn(mockCollection)

    when(mockCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(schemaModel))

    new SchemaValidation(mockConnection)
  }

  val schema = Json.parse("""{
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
                            }""")

  "Calling .loadSchema" should {

    "with a matching schema in mongo" should {
      lazy val validation = setupMocks(List(SchemaModel("route 1", schema)))

      "return a json schema" in {
        lazy val result = validation.loadSchema("route 1")
        await(result).isInstanceOf[JsonSchema]
      }
    }

    "without a matching schema in mongo" should {
      lazy val validation = setupMocks(List.empty)

      "throw an exception" in {
        lazy val ex = intercept[Exception] {
          await(validation.loadSchema("route 1"))
        }

        ex.getMessage shouldEqual "No schema for route in mongo"
      }
    }
  }

  "Calling .validateJson" should {

    "with a valid json body" should {

      lazy val validation = setupMocks(List(SchemaModel("route 1", schema)))
      val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")

      lazy val result = validation.validateJson("route 1", json)

      "return true" in {
        await(result) shouldEqual true
      }
    }

    "with an invalid json body" should {

      lazy val validation = setupMocks(List(SchemaModel("route 1", schema)))
      val json = Json.parse("""{ "firstName" : "Bob" }""")

      lazy val result = validation.validateJson("route 1", json)

      "return false" in {
        await(result) shouldEqual false
      }
    }

    "without a matching schema in mongo" should {
      lazy val validation = setupMocks(List.empty)
      val json = Json.parse("""{ "firstName" : "Bob", "lastName" : "Bobson" }""")

      lazy val result = validation.validateJson("route 1", json)

      "return false" in {
        await(result) shouldEqual false
      }
    }
  }
}
