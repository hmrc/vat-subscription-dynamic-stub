/*
 * Copyright 2020 HM Revenue & Customs
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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.test.UnitSpec
import utils.SchemaValidation

import scala.concurrent.Future

trait MockSchemaValidation extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockSchemaValidation: SchemaValidation = mock[SchemaValidation]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSchemaValidation)
  }

  def mockLoadResponseSchema(schemaId: String)(response: JsonSchema): Unit = {
    when(mockSchemaValidation.loadResponseSchema(ArgumentMatchers.eq(schemaId)))
      .thenReturn(Future.successful(response))
  }

  def mockValidateResponseJson(schemaId: String, json: Option[JsValue])(response:Boolean): Unit = {
    when(mockSchemaValidation.validateResponseJson(ArgumentMatchers.eq(schemaId), ArgumentMatchers.eq(json)))
      .thenReturn(Future.successful(response))
  }

  def mockLoadUrlRegex(schemaId:String)(response: String): Unit ={
    when(mockSchemaValidation.loadUrlRegex(ArgumentMatchers.eq(schemaId)))
      .thenReturn(Future.successful(response))
  }

  def mockValidateUrlMatch(schemaId:String, url:String)(response:Boolean): Unit = {
    when(mockSchemaValidation.validateUrlMatch(ArgumentMatchers.eq(schemaId), ArgumentMatchers.eq(url)))
      .thenReturn(Future.successful(response))
  }

  def mockLoadRequestSchema(requestSchema: JsValue)(response: JsonSchema): Unit = {
    when(mockSchemaValidation.loadRequestSchema(ArgumentMatchers.eq(requestSchema)))
      .thenReturn(Future.successful(response))
  }

  def mockValidateRequestJson(schemaId: String, json: Option[JsValue])(response: Boolean): Unit = {
    when(mockSchemaValidation.validateRequestJson(ArgumentMatchers.eq(schemaId), ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))
  }

}
