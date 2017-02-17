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

package actions

import javax.inject.{Inject, Singleton}

import models.{RouteExceptionKeyModel, RouteExceptionModel}
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.{ActionBuilder, Request, Result, Results}
import repositories.RouteExceptionRepository
import uk.gov.hmrc.domain.Nino

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NinoExceptionTriggersActions @Inject()(exceptionsRepository: RouteExceptionRepository) {

  case class WithNinoExceptionTriggers(nino: Nino, routeId: Option[String]) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(nino, routeId, request, block)
    }

    def processException[A](nino: Nino, routeId: Option[String], request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      val searchCriteria = RouteExceptionKeyModel(nino.nino, routeId)
      exceptionsRepository().findLatestVersionBy(searchCriteria).flatMap { exceptions =>
        exceptions.headOption.fold(block(request)) {
          case RouteExceptionModel(_, _, Status.NOT_FOUND) => Future.successful(Results.NotFound(Json.toJson("Not found error")))
          case RouteExceptionModel(_, _, Status.BAD_GATEWAY) => Future.successful(Results.BadGateway(Json.toJson("Bad gateway error")))
          case RouteExceptionModel(_, _, Status.BAD_REQUEST) => Future.successful(Results.BadRequest(Json.toJson("Bad request error")))
          case RouteExceptionModel(_, _, Status.INTERNAL_SERVER_ERROR) => Future.successful(Results.InternalServerError(Json.toJson("Internal server error")))
          case RouteExceptionModel(_, _, Status.SERVICE_UNAVAILABLE) => Future.successful(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
          case RouteExceptionModel(_, _, Status.REQUEST_TIMEOUT) => Future.successful(Results.RequestTimeout(Json.toJson("Timeout error")))
        }
      }
    }
  }

}
