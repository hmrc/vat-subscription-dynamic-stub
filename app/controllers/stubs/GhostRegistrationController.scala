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
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Result}
import repositories.NonResidentBusinessPartnerRepository
import uk.gov.hmrc.play.microservice.controller.BaseController
import utils.SchemaValidation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class GhostRegistrationController @Inject()(repository: NonResidentBusinessPartnerRepository,
                                            sapHelper: SapHelper,
                                            guardedActions: ExceptionTriggersActions,
                                            schemaValidation: SchemaValidation) extends BaseController {

  val invalidJsonBodySub = Json.toJson("not valid json")

  val registerBusinessPartner: Action[AnyContent] = {
    guardedActions.WithFullDetailsExceptionTriggers(RouteIds.registerIndividualWithoutNino).async {
      implicit request => {

        val body = request.body.asJson
        val validJsonFlag = schemaValidation.validateJson(RouteIds.registerIndividualWithoutNino, body.getOrElse(invalidJsonBodySub))

        def handleJsonValidity(flag: Boolean): Future[Result] = {
          if (flag) {
            val registrationDetails = FullDetailsModel.asModel(body.get)

            Logger.info(s"Successfully read request body as $registrationDetails")

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
          else {
            Future.successful(BadRequest("Request body's JSON failed to validate against the non-UTR individual registration schema"))
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
