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

package controllers.stubs

import com.google.inject.{Inject, Singleton}
import models.{EnrolmentIssuerRequestModel, EnrolmentSubscriberRequestModel}
import play.api.Logger
import play.api.mvc.{Action, AnyContent}
import repository.TaxEnrolmentConnector
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class TaxEnrolmentsController @Inject()(cgtMongoConnector: TaxEnrolmentConnector) extends BaseController {

  def subscribeIssuer(subscriptionId: String): Action[AnyContent] = Action.async {
    implicit request =>

      Logger.warn("Received a call from the back end to make an enrolment issuer request")

      Try {
        val body = request.body.asJson
        val recordData = body.get.as[EnrolmentIssuerRequestModel]

        cgtMongoConnector.issuerRepository.addEntry(recordData)
      } match {
        case Success(_) => Future.successful(NoContent)
        case Failure(exception) => Future.successful(BadRequest)
      }
  }

  def subscribeSubscriber(subscriptionId: String): Action[AnyContent] = Action.async {

    implicit request =>

      Logger.info("Received a call from the back end to make an enrolment subscriber request")

      Try {
        val body = request.body.asJson
        val recordData = body.get.as[EnrolmentSubscriberRequestModel]
        cgtMongoConnector.subscriberRepository.addEntry(recordData)
      } match {
        case Success(_) => Future.successful(NoContent)
        case Failure(exception) => Future.successful(BadRequest)
      }
  }

}
