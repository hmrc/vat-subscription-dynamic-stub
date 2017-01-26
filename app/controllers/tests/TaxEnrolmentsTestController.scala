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

import com.google.inject.Inject
import models.{Identifier, SubscriptionIssuerRequest, SubscriptionSubscriberRequest}
import play.api.mvc.Action
import repository.SubscriptionTaxEnrolmentConnector
import uk.gov.hmrc.play.microservice.controller.BaseController
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class TaxEnrolmentsTestController @Inject()(cgtMongoConnector: SubscriptionTaxEnrolmentConnector) extends BaseController {

  val addSubscriptionIssuerRecord = Action.async{
    implicit request =>
      Try{
        val body = request.body.asJson
        val recordData = body.get.as[SubscriptionIssuerRequest]
        cgtMongoConnector.issuerRepository.addEntry(recordData)
      } match {
        case Success(_) => Future.successful(Ok("Success"))
        case Failure(_) => Future.successful(BadRequest("Could not store data"))
      }
  }

  val removeSubscriptionIssuerRecord = Action.async{
    implicit request =>
      Try{
        val body = request.body.asJson
        val recordData = body.get.as[Identifier]

        cgtMongoConnector.issuerRepository.removeBy(recordData)
      } match {
        case Success(_) => Future.successful(Ok("Success"))
        case Failure(_) => Future.successful(BadRequest("Could not delete data"))
      }
  }

  val addSubscriptionSubscriberRecord = Action.async {
    implicit request =>
      Try{
        val body = request.body.asJson
        val recordData = body.get.as[SubscriptionSubscriberRequest]
        cgtMongoConnector.subscriberRepository.addEntry(recordData)
      } match {
        case Success(_) => Future.successful(Ok("Success"))
        case Failure(_) => Future.successful(BadRequest("Could not store data"))
      }
  }

  val removeSubscriptionSubscriberRecord = Action.async {
    implicit request =>
      Try{
        val body = request.body.asJson
        val recordData = body.get.as[String]
        cgtMongoConnector.subscriberRepository.removeBy(recordData)
      } match {
        case Success(_) => Future.successful(Ok("Success"))
        case Failure(_) => Future.successful(BadRequest("Could not store data"))
      }
  }
}