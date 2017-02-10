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
import helpers.{CGTRefHelper, CompanyErrorSafeId}
import models.{CompanySubmissionModel, SubscriberModel}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import repository.SubscriptionMongoConnector
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class CompanySubscriptionController @Inject()(subscriptionMongoConnector: SubscriptionMongoConnector,
                                              cGTRefHelper: CGTRefHelper
                                             ) extends BaseController {


  def validateBody(model: CompanySubmissionModel): Boolean = {
    Logger.info("Checking the supplied CompanySubmissionModel has all fields defined")
    model.sap.isDefined && model.registeredAddress.isDefined && model.contactAddress.isDefined
  }

  def checkExceptionTriggers(sap: String): Option[Result] = {
    Logger.info("Checking the supplied sap against the company exception trigger sap's")
    sap match {
      case CompanyErrorSafeId.notFound.sap => Some(Results.NotFound(Json.toJson("Not found error")))
      case CompanyErrorSafeId.badRequest.sap => Some(Results.BadRequest(Json.toJson("Bad request error")))
      case CompanyErrorSafeId.badGateway.sap => Some(Results.BadGateway(Json.toJson("Bad gateway error")))
      case CompanyErrorSafeId.internalServerError.sap => Some(Results.InternalServerError(Json.toJson("Internal server error")))
      case CompanyErrorSafeId.serviceUnavailable.sap => Some(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
      case CompanyErrorSafeId.timeout.sap => Some(Results.RequestTimeout(Json.toJson("Timeout error")))
      case _ =>
        Logger.info("No exceptions triggered from the supplied sap of: " + sap)
        None
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

  def returnBody(model: CompanySubmissionModel): Future[Result] = {

    if (validateBody(model)) {
      checkExceptionTriggers(model.sap.get) match {
        case Some(response) => Future.successful(response)
        case _ => returnSubscriptionReference(model.sap.get)
      }
    }
    else Future.successful(Results.BadRequest(Json.toJson("Body of request did not contain the expected values for the company submission model")))
  }

  def subscribe(): Action[AnyContent] = {
    Action.async {
      implicit request => {
        Logger.info("Received a call from the back end to subscribe a Company")
        val companySubmissionModel = request.body.asJson.get.as[CompanySubmissionModel]
        returnBody(companySubmissionModel)
      }
    }
  }
}
