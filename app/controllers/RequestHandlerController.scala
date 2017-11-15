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

package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{Action, AnyContent}
import repositories.DataRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RequestHandlerController @Inject()(dataRepository: DataRepository) extends BaseController {

  def requestHandler(url: String): Action[AnyContent] = Action.async {
    implicit request => {
      dataRepository().find("_id" -> s"""${request.uri}""", "method" -> request.method).map {
        stubData => stubData.nonEmpty match {
          case true => stubData.head.response.isEmpty match {
            case true => Status(stubData.head.status) //Only return status, no body.
            case _ => Status(stubData.head.status)(stubData.head.response.get) //return status and body
          }
          case _ => BadRequest(s"Could not find endpoint in Dynamic Stub matching the URI: ${request.uri}")
        }
      }
    }
  }
}
