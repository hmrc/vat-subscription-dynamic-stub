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

import com.google.inject.{Inject, Singleton}
import models.BusinessPartnerModel
import play.api.mvc.{Action, AnyContent}
import repository.BPMongoConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class RegistrationTestController @Inject()(bpMongoConnector: BPMongoConnector) extends BaseController {

  val addRegistrationRecord: Action[AnyContent] = Action.async { implicit request =>
    Try {
      val body = request.body.asJson
      val recordData = body.get.as[BusinessPartnerModel]

      bpMongoConnector.apply().addEntry(recordData)
    } match {
      case Success(_) => Future.successful(Ok("Success"))
      case Failure(_) => Future.successful(BadRequest("Could not store data"))
    }
  }

  val removeRegistrationRecord: Action[AnyContent] = Action.async { implicit request =>
    Try {
      val body = request.body.asJson
      val recordData = body.get.as[Nino]

      bpMongoConnector.apply().removeBy(recordData)
    } match {
      case Success(_) => Future.successful(Ok("Success"))
      case Failure(_) => Future.successful(BadRequest("Could not delete data"))
    }
  }
}
