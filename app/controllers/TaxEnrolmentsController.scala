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

import com.google.inject.{Inject, Singleton}
import models.{Identifier, SubscriptionIssuerRequest, SubscriptionSubscriberRequest}
import play.api.mvc.{Action, BodyParsers}
import play.libs.Json
import repository.{SubscriptionTaxEnrolmentConnector}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class TaxEnrolmentsController @Inject()(cgtMongoConnector: SubscriptionTaxEnrolmentConnector) extends BaseController {

  def subscribeIssuer(subscriptionId: String) = Action.async {
    implicit request =>
    Try{
      val body = request.body.asJson
      val recordData = body.get.as[SubscriptionIssuerRequest]

      cgtMongoConnector.issuerRepository.addEntry(recordData)
    } match {
      case Success(_) => Future.successful(NoContent)
      case Failure(exception) => Future.successful(BadRequest)
    }
  }

  def subscribeSubscriber(subscriptionId: String) = Action.async {

    implicit request =>
        Try{
          val body = request.body.asJson
          val recordData = body.get.as[SubscriptionSubscriberRequest]
          cgtMongoConnector.subscriberRepository.addEntry(recordData)
        } match {
          case Success(_) => Future.successful(NoContent)
          case Failure(exception) => Future.successful(BadRequest)
        }
      /*val subscribeSubscriberRequestBodyJs = request.body.validate[SubscriptionSubscriberRequest]
      subscribeSubscriberRequestBodyJs.fold(
        errors => Future.successful(BadRequest),
        subscriptionSubscriberRequest => {
          cgtMongoConnector.subscriberRepository.addEntry(subscriptionSubscriberRequest)
          Future.successful(NoContent)
          //see TaxEnrolments README.md for response types for subscribeIssuer
        }
      )*/
  }

}
