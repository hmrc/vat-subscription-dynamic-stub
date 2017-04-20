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

package utils2

import javax.inject.{Inject, Singleton}

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import play.api.Logger
import play.api.libs.json.JsValue
import repositories.SchemaRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SchemaValidation @Inject()(repository: SchemaRepository) {

  private final lazy val jsonMapper = new ObjectMapper()
  private final lazy val jsonFactory = jsonMapper.getFactory

  def loadSchema(routeId: String): Future[JsonSchema] = {
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory

    repository().findLatestVersionBy(routeId).map { models =>
      if (models.isEmpty) {
        throw new Exception("No schema for route in mongo")
      } else {
        val schemaParser: JsonParser = factory.createParser(models.head.schema.toString())
        val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
        val schemaFactory = JsonSchemaFactory.byDefault()
        schemaFactory.getJsonSchema(schemaJson)
      }
    }
  }

  def validateJson(routeId: String, json: JsValue): Future[Boolean] = {
    loadSchema(routeId).map { schema =>
      val jsonParser = jsonFactory.createParser(json.toString())
      val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
      val report = schema.validate(jsonNode)
      report.isSuccess
    } recover {
      case ex => Logger.warn(s"Error parsing json: ${ex.getMessage}")
        false
    }
  }
}
