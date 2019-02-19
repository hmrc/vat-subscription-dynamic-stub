/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import models.SchemaModel
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent}
import repositories.SchemaRepository
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class SetupSchemaController @Inject()(schemaRepository: SchemaRepository) extends BaseController {

  val addSchema: Action[JsValue] = Action.async(parse.json) {
    implicit request => withJsonBody[SchemaModel](
      json => {
        schemaRepository().addEntry(json).map(_.ok match {
          case true => Ok(s"Successfully added Schema: ${request.body}")
          case _ => InternalServerError("Could not store data")
        })
      }
    ).recover {
      case _ => BadRequest("Error Parsing Json SchemaModel")
    }
  }

  val removeSchema: String => Action[AnyContent] = id => Action.async {
    implicit request =>
      schemaRepository().removeById(id).map(_.ok match {
        case true => Ok("Success")
        case _ => InternalServerError("Could not delete data")
      })
  }

  val removeAll: Action[AnyContent] = Action.async {
    implicit request =>
      schemaRepository().removeAll().map(_.ok match {
        case true => Ok("Removed All Schemas")
        case _ => InternalServerError("Unexpected Error Clearing MongoDB.")
      })
  }
}
