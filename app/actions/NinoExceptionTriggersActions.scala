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

import com.google.inject.{Inject, Singleton}
import helpers.ErrorNino
import play.api.libs.json.Json
import play.api.mvc.{ActionBuilder, Request, Result, Results}
import uk.gov.hmrc.domain.Nino

import scala.concurrent.Future

@Singleton
class NinoExceptionTriggersActions @Inject()() {

  case class WithNinoExceptionTriggers(nino: Nino) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      processException(nino, request, block)
    }

    def processException[A](nino: Nino, request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      nino match {
        case Nino(ErrorNino.notFoundNino.nino) => Future.successful(Results.NotFound(Json.toJson("Not found error")))
        case Nino(ErrorNino.badGateway.nino) => Future.successful(Results.BadGateway(Json.toJson("Bad gateway error")))
        case Nino(ErrorNino.badRequest.nino) => Future.successful(Results.BadRequest(Json.toJson("Bad request error")))
        case Nino(ErrorNino.internalServerError.nino) => Future.successful(Results.InternalServerError(Json.toJson("Internal server error")))
        case Nino(ErrorNino.serviceUnavailable.nino) => Future.successful(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
        case Nino(ErrorNino.timeout.nino) => Future.successful(Results.RequestTimeout(Json.toJson("Timeout error")))
        case _ => block(request)
      }
    }
  }
}
