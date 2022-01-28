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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.github.bjansen.ssv.SwaggerValidator
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchema, JsonSchemaFactory}
import models.SchemaModel
import play.api.libs.json.JsValue
import repositories.SchemaRepository

import java.io.StringReader
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SchemaValidation @Inject()(repository: SchemaRepository) extends LoggerUtil {

  private final lazy val jsonMapper = new ObjectMapper()
  private final lazy val jsonFactory = jsonMapper.getFactory

  def validateAgainstJson(schemaModel: SchemaModel, json: JsValue): Boolean = {
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(schemaModel.responseSchema.toString)
    val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
    val schema = JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
    val jsonParser = jsonFactory.createParser(json.toString)
    val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
    schema.validate(jsonNode).isSuccess
  }

  def validateAgainstYaml(schemaModel: SchemaModel, json: JsValue): Boolean = {
    val yamlSchema = (schemaModel.responseSchema \ "value").as[String]
    val validator: SwaggerValidator = SwaggerValidator.forYamlSchema(new StringReader(yamlSchema))
    val successReport: ProcessingReport = validator.validate(json.toString(), "/components/schemas/successResponse")
    val failureReport: ProcessingReport = validator.validate(json.toString(), "/components/schemas/failureResponse")
    successReport.isSuccess | failureReport.isSuccess
  }

  def validateResponse(schemaId: String, json: Option[JsValue]): Future[Boolean] =
    json.fold(Future.successful(true)) { data =>
      repository().findById(schemaId).map { schema =>
        if (schema.schemaType.contains("yaml")) validateAgainstYaml(schema, data)
        else validateAgainstJson(schema, data)
      }
    }

  def loadRequestSchema(requestSchema: JsValue): JsonSchema = {
    val schemaMapper = new ObjectMapper()
    val factory = schemaMapper.getFactory
    val schemaParser: JsonParser = factory.createParser(requestSchema.toString)
    val schemaJson: JsonNode = schemaMapper.readTree(schemaParser)
    JsonSchemaFactory.byDefault().getJsonSchema(schemaJson)
  }

  def validateRequestJson(schemaId: String, json: Option[JsValue]): Future[Boolean] = {
    repository().findById(schemaId).map { schema =>
      if(schema.requestSchema.isDefined) {
        json.fold(true) {
          response =>
            val jsonParser = jsonFactory.createParser(response.toString)
            val jsonNode: JsonNode = jsonMapper.readTree(jsonParser)
            val result: ProcessingReport = loadRequestSchema(schema.requestSchema.get).validate(jsonNode)
            if(!result.isSuccess) {
              logger.warn("Request did not validate against schema: " + result.toString)
              false
            } else true
        }
      } else {
        true
      }
    } recover {
      case ex => throw new Exception(s"Schema could not be retrieved due to exception: ${ex.getMessage}")
    }
  }

  def loadUrlRegex(schemaId: String): Future[String] =
    repository().findById(schemaId).map(_.url)

  def validateUrlMatch(schemaId: String, url: String): Future[Boolean] =
    loadUrlRegex(schemaId).map(regex => url.matches(regex))
}
