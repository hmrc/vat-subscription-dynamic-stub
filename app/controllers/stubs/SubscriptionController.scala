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
import helpers.CgtRefHelper
import models.{SubscribeModel, SubscriberModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import repositories.SubscriptionRepository
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.SchemaValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SubscriptionController @Inject()(repository: SubscriptionRepository,
                                       cgtRefHelper: CgtRefHelper,
                                       guardedActions: ExceptionTriggersActions,
                                       schemaValidation: SchemaValidation) extends BaseController {

  val invalidJsonBodySub = Json.toJson("")

  val subscribe: String => Action[AnyContent] = safeId => {
    guardedActions.ExceptionTriggers(safeId, RouteIds.subscribe).async {
      implicit request => {

        Logger.info("Received a call from the back end to subscribe an Individual")

        val body = request.body.asJson
        val validJsonFlag = schemaValidation.validateJson(RouteIds.subscribe, body.getOrElse(invalidJsonBodySub))

        def handleJsonValidity(flag: Boolean): Future[Result] = {
          if (flag){
            val subscriptionDetails = body.get.as[SubscribeModel]
            val subscriber = repository().findLatestVersionBy(subscriptionDetails.sap)

            def getReference(subscriber: List[SubscriberModel]): Future[String] = {
              if (subscriber.isEmpty) {
                val reference = cgtRefHelper.generateCGTReference()
                repository().addEntry(SubscriberModel(safeId, reference))
                Future.successful(cgtRefHelper.generateCGTReference())
              } else {
                Future.successful(subscriber.head.reference)
              }
            }

            for {
              checkSubscribers <- subscriber
              reference <- getReference(checkSubscribers)
            } yield Ok(Json.toJson(reference))
        }
        else {
          Future.successful(BadRequest("JSON Body failure to validate against requirements of schema for individual subscription"))
        }
      }
        for {
          flag <- validJsonFlag
          result <- handleJsonValidity(flag)
        } yield result
      }
    }
  }
}
