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

import javax.inject.{Inject, Singleton}

import actions.ExceptionTriggersActions
import common.RouteIds
import models.{EnrolmentIssuerRequestModel, EnrolmentSubscriberRequestModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import repositories.{TaxEnrolmentIssuerRepository, TaxEnrolmentSubscriberRepository}
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.SchemaValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class TaxEnrolmentsController @Inject()(subscriberRepository: TaxEnrolmentSubscriberRepository,
                                        issuerRepository: TaxEnrolmentIssuerRepository,
                                        guardedActions: ExceptionTriggersActions,
                                        schemaValidation: SchemaValidation)
  extends BaseController {

  val invalidJsonBodySub = Json.toJson("")

  def subscribeIssuer(subscriptionId: String): Action[AnyContent] = guardedActions.ExceptionTriggers(subscriptionId, RouteIds.taxEnrolmentIssuer).async {
    implicit request => {

      Logger.warn("Received a call from the back end to make an enrolment issuer request")

      val body = request.body.asJson
      val flag = schemaValidation.validateJson(RouteIds.taxEnrolmentIssuer, body.getOrElse(invalidJsonBodySub))

      def handleJsonValidity(flag: Boolean): Result = {
        if (flag) {
          val recordData = body.get.as[EnrolmentIssuerRequestModel]

          Logger.warn(s"Successfully read request body as $recordData")
          issuerRepository().addEntry(recordData)
          NoContent
        }
        else {
          BadRequest("Json does not conform to schema")
        }
      }
      flag.map { x => handleJsonValidity(x) }
    }
  }

  def subscribeSubscriber(subscriptionId: String): Action[AnyContent] = guardedActions.ExceptionTriggers(subscriptionId, RouteIds.taxEnrolmentSubscribe) {

    implicit request =>

      Logger.info("Received a call from the back end to make an enrolment subscriber request")

      Try {
        val body = request.body.asJson
        val recordData = body.get.as[EnrolmentSubscriberRequestModel]

        Logger.warn(s"Successfully read request body as $recordData")
        subscriberRepository().addEntry(recordData)
      } match {
        case Success(_) => NoContent
        case Failure(exception) => BadRequest(exception.getMessage)
      }
  }

}
