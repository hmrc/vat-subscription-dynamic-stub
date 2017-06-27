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

package controllers

import javax.inject.{Inject, Singleton}

import models.SchemaModel
import play.api.mvc.{Action, AnyContent}
import repositories.SchemaRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class SetupSchemaController @Inject()(schemaRepository: SchemaRepository) extends BaseController {

  val addSchema: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(request.body.asJson.fold(BadRequest("Empty Json Body")) {
      json =>
        json.validate[SchemaModel].fold(
          invalid =>
            BadRequest("Json Parse Error, Invalid Schema Request."),
          valid =>
            Try {
              schemaRepository().addEntry(valid)
            } match {
              case Success(_) => Ok("Success")
              case Failure(_) => BadRequest("Could not store data")
            }
        )
    })
  }

  val removeSchema: String => Action[AnyContent] = { id =>
    Action.async { implicit request =>
      Try {
        schemaRepository().removeBy(id)
      } match {
        case Success(_) => Future.successful(Ok("Success"))
        case Failure(ex) => ex.printStackTrace()
          Future.successful(BadRequest("Could not delete data"))
      }
    }
  }

  val removeAll = Action.async { implicit request =>
    Try {
      schemaRepository().removeAll()
    } match {
      case Success(_) => Future.successful(Ok("Removed All Schemas"))
      case Failure(_) => Future.successful(InternalServerError("Unexpected Error Clearing MongoDB."))
    }
  }
}
