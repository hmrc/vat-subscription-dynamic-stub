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
import models.{CompanySubmissionModel, SubscriberModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import repositories.SubscriptionRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompanySubscriptionController @Inject()(subscriptionMongoConnector: SubscriptionRepository,
                                              cGTRefHelper: CgtRefHelper,
                                              guardedActions: ExceptionTriggersActions)
  extends BaseController {

  private val noContactAddressMessage = "Body of request did not contain the expected values for the company submission model"

  def subscribe(): Action[AnyContent] = {
    guardedActions.WithFullDetailsExceptionTriggers(RouteIds.companySubscribe).async {
      implicit request => {

        Logger.info("Received a call from the back end to subscribe a Company")
        val model = request.body.asJson.get.as[CompanySubmissionModel]

        if (model.contactAddress.isDefined) returnSubscriptionReference(model.sap.get)
        else Future.successful(Results.BadRequest(Json.toJson(noContactAddressMessage)))
      }
    }
  }

  def returnSubscriptionReference(sap: String): Future[Result] = {
    val subscriber = subscriptionMongoConnector.repository.findLatestVersionBy(sap)

    def getReference(subscriber: List[SubscriberModel]): Future[String] = {
      if (subscriber.isEmpty) {
        val reference = cGTRefHelper.generateCGTReference()
        Logger.info("Generating a new entry ")
        subscriptionMongoConnector.repository.addEntry(SubscriberModel(sap, reference))
        Future.successful(reference)
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
