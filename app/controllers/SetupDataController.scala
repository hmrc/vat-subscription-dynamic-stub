/*
 * Copyright 2023 HM Revenue & Customs
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
import models.HttpMethod._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result}
import services.DataService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.{LoggerUtil, SchemaValidation}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SetupDataController @Inject()(schemaValidation: SchemaValidation,
                                    DataRepository: DataService,
                                    cc: ControllerComponents)
                                   (implicit ec: ExecutionContext) extends BackendController(cc) with LoggerUtil {

  val addData: Action[JsValue] = Action.async(parse.json) {
    implicit request => withJsonBody[DataModel](
      json => json.method.toUpperCase match {
        case GET | POST | PUT =>
          schemaValidation.validateUrlMatch(json.schemaId, json._id) flatMap {
            case true =>
              schemaValidation.validateResponse(json.schemaId, json.response) flatMap {
                case true => addStubDataToDB(json)
                case false =>
                  val message = s"The Json Body:\n\n${json.response.get} did not validate against the Schema Definition"
                  logger.warn(s"[SetupDataController][addData] - $message")
                  Future.successful(BadRequest(message))
              }
            case false =>
              schemaValidation.loadUrlRegex(json.schemaId) map { regex =>
                val message = s"URL ${json._id} did not match the Schema Definition Regex $regex"
                logger.warn(s"[SetupDataController][addData] - $message")
                BadRequest(message)
              }
          }
        case x =>
          val message = s"The method: $x is currently unsupported"
          logger.warn(s"[SetupDataController][addData] - $message")
          Future.successful(BadRequest(message))
      }
    ).recover {
      case ex =>
        val message = s"Error Parsing Json DataModel due to exception: ${ex.getMessage}"
        logger.warn(s"[SetupDataController][addData] - $message")
        InternalServerError(message)
    }
  }

  private def addStubDataToDB(json: DataModel): Future[Result] = {
    DataRepository.addEntry(json).map {
      case result if result.wasAcknowledged() => Ok(s"The following JSON was added to the stub: \n\n${Json.toJson(json)}")
      case _ =>
        val message = "Failed to add data to Stub."
        logger.warn(s"[SetupDataController][addStubDataToDB] - $message")
        InternalServerError(message)
    }
  }

  val removeData: String => Action[AnyContent] = url => Action.async {
    DataRepository.removeById(url).map {
        case result if result.wasAcknowledged() => Ok("Success")
        case _ =>
          val message = "Could not delete data"
          logger.warn(s"[SetupDataController][removeData] - $message")
          InternalServerError(message)
      }
  }

  val removeAll: Action[AnyContent] = Action.async {
    DataRepository.removeAll().map {
        case result if result.wasAcknowledged() => Ok("Removed All Stubbed Data")
        case _ =>
          val message = "Unexpected Error Clearing MongoDB."
          logger.warn(s"[SetupDataController][removeAll] - $message")
          InternalServerError(message)
      }
  }
}
