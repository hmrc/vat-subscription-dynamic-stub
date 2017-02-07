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

import actions.NinoExceptionTriggersActions
import com.google.inject.{Inject, Singleton}
import helpers.SAPHelper
import models.{BusinessPartnerModel, RegisterModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import repository.BPMongoConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.microservice.controller.BaseController
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RegistrationController @Inject()(bpMongoConnector: BPMongoConnector,
                                       sAPHelper: SAPHelper,
                                       ninoExceptionTriggersActions: NinoExceptionTriggersActions) extends BaseController {

  val registerBusinessPartner: String => Action[AnyContent] = {
    nino => ninoExceptionTriggersActions.WithNinoExceptionTriggers(Nino(nino)).async {
      implicit request => {

        Logger.warn("Received a call from the back end to register")

        val body = request.body.asJson
        val registrationDetails = body.get.as[RegisterModel]

        Logger.warn("Opening a connection to mongo.")

        val businessPartner = bpMongoConnector.repository.findLatestVersionBy(registrationDetails.nino)

        Logger.warn("Connection established to mongo.")

        def getReference(bp: List[BusinessPartnerModel]): Future[String] = {
          if (bp.isEmpty) {
            Logger.warn("Created a new entry with sap")
            val sap = sAPHelper.generateSap()
            for {
              mongo <- bpMongoConnector.repository.addEntry(BusinessPartnerModel(registrationDetails.nino, sap))
            } yield sap

          } else {
            Logger.warn("Created a new entry with sap " + bp.head.sap)
            Future.successful(bp.head.sap)
          }
        }

        for {
          bp <- businessPartner
          sap <- getReference(bp)
        } yield Ok(Json.toJson(sap))
      }
    }
  }
}
