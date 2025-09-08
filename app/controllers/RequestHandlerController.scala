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

import models.DataModel
import javax.inject.{Inject, Singleton}
import models.HttpMethod._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, ControllerComponents, Result, Results}
import services.DataService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.SchemaValidation
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestHandlerController @Inject()(schemaValidation: SchemaValidation,
                                         DataRepository: DataService,
                                         cc: ControllerComponents)(implicit ec: ExecutionContext) extends BackendController(cc) {

  def getRequestHandler(url: String): Action[AnyContent] = Action.async {
    implicit request => {
      DataRepository.find(Seq("_id" -> request.uri, "method" -> GET)).map {
        case head :: _ if head.response.nonEmpty => Status(head.status)(head.response.get)
        case head :: _ => Status(head.status)
        case _ => NotFound(errorResponseBody)
      }
    }
  }

  def postRequestHandler(url: String): Action[AnyContent] = requestHandler(url,POST)
  def putRequestHandler(url: String): Action[AnyContent] = requestHandler(url,PUT)

  private def requestHandler(url: String, method: String): Action[AnyContent] = Action.async {
    implicit request =>
    val bodyJson = request.body.asJson
      val idFromBody: Option[String] =
        bodyJson.flatMap { js =>
          (js \ "taxpayerInformation" \ "idNumber").asOpt[String]
        }

      def serve(stub: DataModel): Future[Result] =
        schemaValidation.validateRequestJson(stub.schemaId, bodyJson).map {
          case true =>
            stub.response match {
              case Some(r) => Results.Status(stub.status)(r)
              case None    => Results.Status(stub.status)
            }
          case false =>
            Results.BadRequest(Json.obj("code" -> "400", "reason" -> "Request did not validate against schema"))
        }

      idFromBody match {
        case Some(id) =>
          DataRepository.find(Seq("_id" -> s"${request.uri}::${id}", "method" -> method)).flatMap {
            case stub :: _ => serve(stub)
            case Nil =>
              DataRepository.find(Seq("_id" -> request.uri, "method" -> method)).flatMap {
                case legacy :: _ => serve(legacy)
                case Nil         => Future.successful(NotFound(errorResponseBody))
              }
          }
        case None =>
          DataRepository.find(Seq("_id" -> request.uri, "method" -> method)).flatMap {
            case head :: _ => serve(head)
            case Nil       => Future.successful(NotFound(errorResponseBody))
          }
      }
  }

  val errorResponseBody: JsValue = Json.obj(
    "code" -> "NOT_FOUND",
    "reason" -> "No data exists for this request."
  )

}
