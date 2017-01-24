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
import helpers.SAPHelper
import models.BusinessPartner
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, BodyParsers}
import repository.CGTMongoConnector
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class RegistrationController @Inject()(cgtMongoConnector: CGTMongoConnector[BusinessPartner, Nino], sAPHelper: SAPHelper) extends BaseController {

  def registerBusinessPartner(nino: Nino) = Action.async(BodyParsers.parse.json) { implicit request =>

    val businessPartner = cgtMongoConnector.findLatestVersionBy(nino)

    def getReference(bp: List[BusinessPartner]): Future[String] = {
      if (bp.isEmpty) {
        val sap = sAPHelper.generateSap()
        for {
          mongo <- cgtMongoConnector.addEntry(BusinessPartner(nino, sap))
        } yield sap
      } else {
        Future.successful(bp.head.sap)
      }
    }

    Try {
      for {
        bp <- businessPartner
        sap <- getReference(bp)
      } yield Ok(Json.toJson(sap))
    } match {
      case Success(result) => result
      case Failure(error) => Future.successful(InternalServerError(Json.toJson(error)))
    }
  }}
}
