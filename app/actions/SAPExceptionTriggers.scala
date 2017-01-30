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
import helpers.ErrorSafeId
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class SAPExceptionTriggers @Inject()() {

  case class WithSapExceptionTriggers(sap: String) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      sap match {
        case ErrorSafeId.notFound.sap => Future.successful(Results.NotFound(Json.toJson("Not found error")))
        case ErrorSafeId.badRequest.sap => Future.successful(Results.BadRequest(Json.toJson("Bad request error")))
        case ErrorSafeId.badGateway.sap => Future.successful(Results.BadGateway(Json.toJson("Bad gateway error")))
        case ErrorSafeId.internalServerError.sap => Future.successful(Results.InternalServerError(Json.toJson("Internal server error")))
        case ErrorSafeId.serviceUnavailable.sap => Future.successful(Results.ServiceUnavailable(Json.toJson("Service unavailable error")))
        case ErrorSafeId.timeout.sap => Future.successful(Results.RequestTimeout(Json.toJson("Timeout error")))
        case _ => block(request)
      }
    }
  }

}
