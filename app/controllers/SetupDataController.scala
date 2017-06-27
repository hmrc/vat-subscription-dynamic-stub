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
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Result}
import repositories.DataRepository
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.SchemaValidation
import models.HttpMethod._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class SetupDataController @Inject()(schemaValidation: SchemaValidation,
                                    dataRepository: DataRepository
                                          ) extends BaseController {


  val addData: Action[JsValue] = Action.async(parse.json) {
    implicit request => withJsonBody[DataModel](
      json => json.method.toUpperCase match {
        case GET =>
          schemaValidation.validateUrlMatch(json.schemaId, json._id) flatMap {
            case true =>
              schemaValidation.validateResponseJson(json.schemaId, json.response) map {
                case true => addStubDataToDB(json)
                case false => BadRequest(s"The Json Body:\n\n${json.response} did not validate against the Schema Definition")
              }
            case false =>
              schemaValidation.loadUrlRegex(json.schemaId) map {
                regex => BadRequest(s"URL ${json._id} did not match the Schema Definition Regex $regex")
              }
          }
        case x => Future.successful(BadRequest(s"The method: $x is currently unsupported"))
      }
    )
  }

  private def addStubDataToDB(json: DataModel): Result = {
    Try {
      dataRepository().addEntry(json)
    } match {
      case Success(_) => Ok(s"The following JSON was added to the stub: \n\n${Json.toJson(json)}")
      case Failure(ex) => BadRequest(s"Failed to add data to Stub. Error: ${ex.getMessage}. \n\n Stack Trace: ${ex.getStackTrace}")
    }
  }

  val removeAll = Action.async { implicit request =>
    Try {
      dataRepository().removeAll()
    } match {
      case Success(_) => Future.successful(Ok("Removed All Stubbed Data"))
      case Failure(_) => Future.successful(InternalServerError("Unexpected Error Clearing MongoDB."))
    }
  }
}
