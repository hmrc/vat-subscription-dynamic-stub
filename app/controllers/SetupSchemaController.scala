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

package controllers


import javax.inject.{Inject, Singleton}
import models.SchemaModel
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import services.SchemaService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.LoggerUtil

import scala.concurrent.ExecutionContext

@Singleton
class SetupSchemaController @Inject()(SchemaRepository: SchemaService,
                                      cc: ControllerComponents)
                                     (implicit ec: ExecutionContext) extends BackendController(cc) with LoggerUtil {

  val addSchema: Action[JsValue] = Action.async(parse.json) {
    implicit request => withJsonBody[SchemaModel](
      json => {
        SchemaRepository.addEntry(json).map {
          case result if result.wasAcknowledged() => Ok(s"Successfully added Schema: ${request.body}")
          case _ =>
            val message = "Could not store data"
            logger.warn(s"[SetupSchemaController][addSchema] - $message")
            InternalServerError(message)
        }
      }
    ).recover {
      case ex =>
        val message = s"Schema could not be added due to exception: ${ex.getMessage}"
        logger.warn(s"[SetupSchemaController][addSchema] - $message")
        InternalServerError(message)
    }
  }

  val removeSchema: String => Action[AnyContent] = id => Action.async {
    SchemaRepository.removeById(id).map {
        case result if result.wasAcknowledged() => Ok("Success")
        case _ =>
          val message = "Could not delete data"
          logger.warn(s"[SetupSchemaController][removeSchema] - $message")
          InternalServerError(message)
      }
  }

  val removeAll: Action[AnyContent] = Action.async {
    SchemaRepository.removeAll().map {
        case result if result.wasAcknowledged() => Ok("Removed All Schemas")
        case _ =>
          val message = "Unexpected error clearing MongoDB"
          logger.warn(s"[SetupSchemaController][removeAll] - $message")
          InternalServerError(message)
      }
  }
}
