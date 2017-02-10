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
import models.{CompanySubmissionModel, SubscriberModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import repository.SubscriptionMongoConnector
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future

@Singleton
class CompanySubscriptionController @Inject()(subscriptionMongoConnector: SubscriptionMongoConnector,
                                              cGTRefHelper: CGTRefHelper,
                                              sAPExceptionTriggers: SAPExceptionTriggers
                                             ) extends BaseController {

  def subscribe(model: CompanySubmissionModel): Action[AnyContent] = {

    sAPExceptionTriggers.WithCompanySapExceptionTriggers(model).async {
      implicit request => {

        Logger.info("Received a call from the back end to subscribe a Company")

        val body = request.body.asJson
        val sap = body.get.as[CompanySubmissionModel].sap.get
        val subscriber = subscriptionMongoConnector.repository.findLatestVersionBy(sap)

        def getReference(subscriber: List[SubscriberModel]): Future[String] = {
          if (subscriber.isEmpty) {
            val reference = cGTRefHelper.generateCGTReference()
            subscriptionMongoConnector.repository.addEntry(SubscriberModel(sap, reference))
            Future.successful(cGTRefHelper.generateCGTReference())
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
