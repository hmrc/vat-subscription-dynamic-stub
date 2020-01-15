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

package controllers

import javax.inject.{Inject, Singleton}
import models.HttpMethod._
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import repositories.DataRepository
import uk.gov.hmrc.play.bootstrap.controller.BaseController
import utils.SchemaValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RequestHandlerController @Inject()(schemaValidation: SchemaValidation, dataRepository: DataRepository) extends BaseController {

  def getRequestHandler(url: String): Action[AnyContent] = Action.async {
    implicit request => {
      dataRepository().find("_id" -> s"""${request.uri}""", "method" -> GET).map {
        stubData => if (stubData.nonEmpty) {
          if (stubData.head.response.isEmpty) {
            Status(stubData.head.status) //Only return status, no body.
          } else {
            Status(stubData.head.status)(stubData.head.response.get)  //return status and body
          }
        } else {
          BadRequest(s"Could not find endpoint in Dynamic Stub matching the URI: ${request.uri}")
        }
      }
    }
  }

  def postRequestHandler(url: String): Action[AnyContent] = requestHandler(url,POST)
  def putRequestHandler(url: String): Action[AnyContent] = requestHandler(url,PUT)

  private def requestHandler(url: String, method: String): Action[AnyContent] = Action.async {
    implicit request => {
      dataRepository().find("_id" -> s"""${request.uri}""", "method" -> method).flatMap {
        stubData => if (stubData.nonEmpty) {
          schemaValidation.validateRequestJson(stubData.head.schemaId, request.body.asJson) map {
            case true => if (stubData.head.response.isEmpty) {
              Status(stubData.head.status)
            } else {
              Status(stubData.head.status)(stubData.head.response.get)
            }
            case false => BadRequest(Json.obj("code" -> "400", "reason" -> "Request did not validate against schema"))
          }
        } else {
          Future(BadRequest(s"Could not find endpoint in Dynamic Stub matching the URI: ${request.uri}"))
        }
      }
    }
  }

}
