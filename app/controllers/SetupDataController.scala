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

import models.DataModel
import play.api.libs.json.JsValue
import play.api.mvc.{Action, AnyContent, Result}
import repositories.DataRepository
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.SchemaValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}

@Singleton
class SetupDataController @Inject()(schemaValidation: SchemaValidation,
                                    dataRepository: DataRepository
                                          ) extends BaseController {


  val addData: Action[JsValue] = Action.async(parse.json) {
    implicit request => withJsonBody[DataModel](
      json => json.method.toUpperCase match {
        case "GET" => schemaValidation.validateResponseJson(json._id, json.response.get) map {
          case true => addStubDataToDB(json)
          case false => BadRequest("Failed to Validate Schema")
        }
      }
    )
  }

  private def addStubDataToDB(json: DataModel): Result = {
    Try {
      dataRepository().addEntry(json)
    } match {
      case Success(_) => NoContent
      case Failure(ex) => BadRequest(s"Failed to add data to Stub. Error: ${ex.getMessage}. \n\n Stack Trace: ${ex.getStackTrace}")
    }
  }
}
