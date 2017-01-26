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

import actions.SAPExceptionTriggers
import com.google.inject.{Inject, Singleton}
import helpers.CGTRefHelper
import models.{SubscribeModel, SubscriberModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import repository.SubscriptionMongoConnector
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SubscriptionController @Inject()(subscriptionMongoConnector: SubscriptionMongoConnector,
                                       cGTRefHelper: CGTRefHelper,
                                       sAPExceptionTriggers: SAPExceptionTriggers
                                      ) extends BaseController {

  val subscribe: String => Action[AnyContent] = safeId => {
    sAPExceptionTriggers.WithSapExceptionTriggers(safeId).async {
      implicit request => {

        val body = request.body.asJson
        val subscriptionDetails = body.get.as[SubscribeModel]
        val subscriber = subscriptionMongoConnector.repository.findLatestVersionBy(subscriptionDetails.sap)

        def getReference(subscriber: List[SubscriberModel]): Future[String] = {
          if (subscriber.isEmpty) {
            val reference = cGTRefHelper.generateCGTReference()
            for {
              subscription <- subscriptionMongoConnector.repository.addEntry(SubscriberModel(safeId, reference))
            } yield reference
          } else {
            Future.successful(subscriber.head.reference)
          }
        }

        for {
          checkSubscribers <- subscriber
          reference <- getReference(checkSubscribers)
        } yield Ok(Json.toJson(reference))
      }
    }
  }
}
