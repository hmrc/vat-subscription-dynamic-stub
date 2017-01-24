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

import com.google.inject.Inject
import models.{Error, Identifier, SubscriptionIssuerRequest, SubscriptionSubscriberRequest}
import play.api.mvc.{Action, BodyParsers}
import play.libs.Json
import repository.CGTMongoConnector
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future


class TaxEnrolmentsController @Inject()(cgtMongoConnector: CGTMongoConnector[SubscriptionIssuerRequest, Identifier]) extends BaseController {

  def subscribeIssuer(subscriptionId: String) = Action.async (BodyParsers.parse.json) {
    implicit request =>
      val subscriptionIssuerRequestBodyJs = request.body.validate[SubscriptionIssuerRequest]
      subscriptionIssuerRequestBodyJs.fold(
        errors => Future.successful(BadRequest()),
        subscriptionIssuerRequest => {
          cgtMongoConnector.addEntry(subscriptionIssuerRequest)
          Future.successful(NoContent)
        }
      )
  }

  def subscribeSubscriber(subscriptionId: String) = Action.async (BodyParsers.parse.json) {
    implicit request =>
      val subscribeSubscriberRequestBodyJs = request.body.validate[SubscriptionSubscriberRequest]
      subscribeSubscriberRequestBodyJs.fold(
        errors => Future.successful(BadRequest()),
        subscriptionSubscriberRequest => {
          cgtMongoConnector.addEntry(subscriptionSubscriberRequest)
          Future.successful(NoContent)
        }
      )
  }

}
