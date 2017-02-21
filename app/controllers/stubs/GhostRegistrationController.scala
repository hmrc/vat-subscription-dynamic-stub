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

import actions.ExceptionTriggersActions
import javax.inject.{Inject, Singleton}

import common.RouteIds
import helpers.SapHelper
import models.{FullDetailsModel, NonResidentBusinessPartnerModel}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import repositories.NonResidentBusinessPartnerRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class GhostRegistrationController @Inject()(repository: NonResidentBusinessPartnerRepository,
                                            sapHelper: SapHelper,
                                            guardedActions: ExceptionTriggersActions)
  extends BaseController {

  val registerBusinessPartner: Action[AnyContent] = {
    guardedActions.WithFullDetailsExceptionTriggers(RouteIds.registerIndividualWithoutNino).async {
      implicit request => {

        val body = request.body.asJson
        val registrationDetails = body.get.as[FullDetailsModel]
        val businessPartner = repository().findLatestVersionBy(registrationDetails)

        def getReference(bp: List[NonResidentBusinessPartnerModel]): Future[String] = {
          if (bp.isEmpty) {
            val sap = sapHelper.generateSap()
            for {
              _ <- repository().addEntry(NonResidentBusinessPartnerModel(registrationDetails, sap))
            } yield sap
          } else {
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
