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
import helpers.ErrorFullDetails
import models.FullDetailsModel
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future


@Singleton
class FullDetailsExceptionTriggersActions @Inject()() {

  case class WithFullDetailsExceptionTriggers() extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(request, block)
    }
  }

  def processException[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
    request.asInstanceOf[Request[AnyContent]].body.asJson.get.as[FullDetailsModel] match {
      case ErrorFullDetails.notFound.fullDetails => Future.successful(Results.NotFound(Json.toJson("Not found error")))
      case ErrorFullDetails.badGateway.fullDetails => Future.successful(Results.BadGateway(Json.toJson("Bad gateway error")))
      case ErrorFullDetails.badRequest.fullDetails => Future.successful(Results.BadRequest(Json.toJson("Bad request error")))
      case ErrorFullDetails.internalServerError.fullDetails => Future.successful(Results.InternalServerError(Json.toJson("Internal server error")))
      case ErrorFullDetails.serviceUnavailable.fullDetails => Future.successful(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
      case ErrorFullDetails.timeout.fullDetails => Future.successful(Results.RequestTimeout(Json.toJson("Timeout error")))
      case _ => block(request)
    }
  }
}
