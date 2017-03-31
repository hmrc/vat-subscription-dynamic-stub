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

import javax.inject.{Inject, Singleton}

import actions.ExceptionTriggersActions
import common.RouteIds
import models.{AgentClientSubmissionModel, RelationshipModel}
import play.api.mvc.{Action, AnyContent}
import repositories.AgentClientRelationshipRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class AgentRelationshipController @Inject()(repository: AgentClientRelationshipRepository,
                                            guardedActions: ExceptionTriggersActions
                                           ) extends BaseController {

  def createAgentClientRelationship(arn: String): Action[AnyContent] = {

    guardedActions.AgentExceptionTriggers(RouteIds.createRelationship, arn).async {
      implicit request => {
        Try {
          val body = request.body.asJson.get
          val cgtRef = ((body \ "clientAllocation" \ "identifiers").head \ "value").as[String]
          val serviceName = (body \ "clientAllocation" \ "serviceName").as[String]

          val relationshipModel = RelationshipModel(arn, cgtRef)
          val modelToStore = AgentClientSubmissionModel(relationshipModel, serviceName)

          repository().addEntry(modelToStore)
        } match {
          case Success(_) => Future.successful(NoContent)
          case Failure(e) => Future.successful(BadRequest(s"${e.getMessage}"))
        }
      }
    }
  }

  def createDesAgentClickRelationship: Action[AnyContent] = {
    guardedActions.AgentExceptionTriggers(RouteIds.createDesRelationship, ).async {

    }
  }
}
