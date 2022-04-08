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

package mocks

import com.github.fge.jsonschema.main.JsonSchema
import org.scalamock.handlers.{CallHandler1, CallHandler2}
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.JsValue
import utils.SchemaValidation
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Future

trait MockSchemaValidation extends AnyWordSpec with MockFactory {

  val mockSchemaValidation: SchemaValidation = mock[SchemaValidation]

  def mockValidateResponse(response: Future[Boolean]): CallHandler2[String, Option[JsValue], Future[Boolean]] =
    (mockSchemaValidation.validateResponse(_: String, _: Option[JsValue]))
      .expects(*,*)
      .returning(response)

  def mockLoadUrlRegex(response: String): CallHandler1[String, Future[String]] =
    (mockSchemaValidation.loadUrlRegex(_: String))
      .expects(*)
      .returning(Future.successful(response))

  def mockValidateUrlMatch(response:Boolean): CallHandler2[String, String, Future[Boolean]] =
    (mockSchemaValidation.validateUrlMatch(_: String, _: String))
      .expects(*,*)
      .returning(Future.successful(response))

  def mockLoadRequestSchema(response: JsonSchema): CallHandler1[JsValue, JsonSchema] =
    (mockSchemaValidation.loadRequestSchema(_: JsValue))
      .expects(*)
      .returning(response)

  def mockValidateRequestJson(response: Boolean): CallHandler2[String, Option[JsValue], Future[Boolean]] =
    (mockSchemaValidation.validateRequestJson(_: String, _: Option[JsValue]))
      .expects(*,*)
      .returning(Future.successful(response))
}
