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

import models.{CompanySubmissionModel, FullDetailsModel, RouteExceptionKeyModel, RouteExceptionModel}
import play.api.http.Status
import play.api.libs.json._
import play.api.mvc._
import repositories.RouteExceptionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ExceptionTriggersActions @Inject()(exceptionsRepository: RouteExceptionRepository) {

  private def processException[A](id: String, routeId: String, request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val searchCriteria = RouteExceptionKeyModel(id, routeId)
    exceptionsRepository().findLatestVersionBy(searchCriteria).flatMap { exceptions =>
      exceptions.headOption.fold(block(request)) {
        case RouteExceptionModel(_, _, Status.NOT_FOUND) => Future.successful(Results.NotFound(Json.toJson("Not found error")))
        case RouteExceptionModel(_, _, Status.BAD_GATEWAY) => Future.successful(Results.BadGateway(Json.toJson("Bad gateway error")))
        case RouteExceptionModel(_, _, Status.BAD_REQUEST) => Future.successful(Results.BadRequest(Json.toJson("Bad request error")))
        case RouteExceptionModel(_, _, Status.CONFLICT) => Future.successful(Results.Conflict(Json.toJson("Conflict request error")))
        case RouteExceptionModel(_, _, Status.INTERNAL_SERVER_ERROR) => Future.successful(Results.InternalServerError(Json.toJson("Internal server error")))
        case RouteExceptionModel(_, _, Status.SERVICE_UNAVAILABLE) => Future.successful(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
        case RouteExceptionModel(_, _, Status.REQUEST_TIMEOUT) => Future.successful(Results.RequestTimeout(Json.toJson("Timeout error")))
        case RouteExceptionModel(_, _, httpCode) => Future.successful(new Results.Status(httpCode)(Json.toJson("Not successful")))
      }
    }
  }

  case class ExceptionTriggers(id: String, routeId: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(id, routeId, request, block)
    }
  }

  case class WithFullDetailsExceptionTriggers(routeId: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      val details = request.asInstanceOf[Request[AnyContent]].body.asJson.get.as[FullDetailsModel]
      val uniqueId = s"${details.firstName} ${details.lastName}"
      processException(uniqueId, routeId, request, block)
    }
  }

  case class CompanySubscriptionExceptionTriggers(routeId: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      val details = request.asInstanceOf[Request[AnyContent]].body.asJson.get.as[CompanySubmissionModel]
      details.sap.fold(Future.successful(Results.BadRequest(Json.toJson("SAP not specified")))) {
        id => processException(details.sap.getOrElse(id), routeId, request, block)
      }
    }
  }

  case class AgentExceptionTriggers(routeId: String, arn: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(arn, routeId, request, block)
    }
  }

}
