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

import models._
import play.api.http.Status
import play.api.libs.json._
import play.api.mvc._
import repositories.RouteExceptionRepository
import Status._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class ExceptionTriggersActions @Inject()(exceptionsRepository: RouteExceptionRepository) {

  private def processException[A](id: String, routeId: String, request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    val searchCriteria = RouteExceptionKeyModel(id, routeId)
    exceptionsRepository().findLatestVersionBy(searchCriteria).flatMap { exceptions =>
      exceptions.headOption.fold(block(request)) {
        case RouteExceptionModel(_, _, NOT_FOUND) => constructResponse(NOT_FOUND, "Not found error")
        case RouteExceptionModel(_, _, BAD_GATEWAY) => constructResponse(BAD_GATEWAY, "Bad gateway error")
        case RouteExceptionModel(_, _, BAD_REQUEST) => constructResponse(BAD_REQUEST, "Bad request error")
        case RouteExceptionModel(_, _, CONFLICT) => constructResponse(CONFLICT, "Conflict request error")
        case RouteExceptionModel(_, _, INTERNAL_SERVER_ERROR) => constructResponse(INTERNAL_SERVER_ERROR, "Internal server error")
        case RouteExceptionModel(_, _, SERVICE_UNAVAILABLE) => constructResponse(SERVICE_UNAVAILABLE, "Service unavailable error")
        case RouteExceptionModel(_, _, REQUEST_TIMEOUT) => constructResponse(REQUEST_TIMEOUT, "Timeout error")
        case RouteExceptionModel(_, _, httpCode) => constructResponse(httpCode, "Not successful")
      }
    }
  }

  def constructResponse(code: Int, reason: String): Future[Result] = {
    val body: JsObject = Json.obj("code" -> code.toString, "reason" -> reason)
    Future.successful(Results.Status(code)(body))
  }

  case class ExceptionTriggers(id: String, routeId: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(id, routeId, request, block)
    }
  }

  case class WithFullDetailsExceptionTriggers(routeId: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      val details: FullDetailsModel = request.asInstanceOf[Request[AnyContent]].body.asJson.get
      val uniqueId = s"${details.firstName} ${details.lastName}"
      processException(uniqueId, routeId, request, block)
    }
  }

  case class CompanySubscriptionExceptionTriggers(routeId: String, sap: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(sap, routeId, request, block)
    }
  }

  case class AgentExceptionTriggers(routeId: String, arn: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(arn, routeId, request, block)
    }
  }

  case class DesAgentExceptionTriggers(routeId: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A] => Future[Result])): Future[Result] = {
      val relationshipModel = request.asInstanceOf[Request[AnyContent]].body.asJson.get.as[RelationshipModel]
      val uniqueId = relationshipModel.arn
      processException(uniqueId, routeId, request, block)
    }
  }

}
