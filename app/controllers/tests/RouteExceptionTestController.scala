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

package controllers.tests

import javax.inject.{Inject, Singleton}

import models.{RouteExceptionKeyModel, RouteExceptionModel}
import play.api.Logger
import play.api.mvc.{Action, AnyContent}
import repositories.RouteExceptionRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class RouteExceptionTestController @Inject()(repository: RouteExceptionRepository) extends BaseController {

  val addException: Action[AnyContent] = Action.async { implicit request =>
    Try {
      val body = request.body.asJson
      val document = body.get.as[RouteExceptionModel]

      repository().addEntry(document)
    } match {
      case Success(_) => Future.successful(Ok("Success"))
      case Failure(_) => Future.successful(BadRequest("Could not store data"))
    }

  }

  val removeException: Action[AnyContent] = Action.async { implicit request =>
    Try {
      Logger.info(s"""Request = "${request.body}" """)
      val body = request.body.asJson

      Logger.info("#########################")
      Logger.info(body.mkString(" @ "))
      Logger.info(s"""Request = "${request.body}" """)
      val document = body.get.as[RouteExceptionKeyModel]

      repository().removeBy(document)
    } match {
      case Success(_) => Future.successful(Ok("Success"))
      case Failure(ex) => {
        ex.printStackTrace()
        Future.successful(BadRequest("Could not delete data"))
      }
    }
  }
}
