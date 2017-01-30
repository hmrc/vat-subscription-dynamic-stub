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

import com.google.inject.{Inject, Singleton}
import play.api.mvc.{Action, AnyContent, Result}
import repository.{BPMongoConnector, SubscriptionMongoConnector, TaxEnrolmentConnector}
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class ClearDownController @Inject()(
                                     bPMongoConnector: BPMongoConnector,
                                     subscriptionETMPMongoConnector: SubscriptionMongoConnector,
                                     subscriptionTaxEnrolmentConnector: TaxEnrolmentConnector
                                   ) extends BaseController {

  private val clearResult: Future[Unit] => Future[Result] = clearAction =>
    Try {
      clearAction
    } match {
      case Success(_) => Future.successful(Ok("Success"))
      case Failure(_) => Future.successful(BadRequest("Could not delete data"))
    }

  def clearRegistration: Future[Result] = clearResult(bPMongoConnector.repository.removeAll())

  def clearSubscription: Future[Result] = clearResult(subscriptionETMPMongoConnector.repository.removeAll())

  def clearEnrolmentSubscription: Future[Result] = clearResult(subscriptionTaxEnrolmentConnector.subscriberRepository.removeAll())

  def clearEnrolmentIssuer: Future[Result] = clearResult(subscriptionTaxEnrolmentConnector.issuerRepository.removeAll())

  def checkForFailed(seq: Seq[Result]): Future[Boolean] = {
    //I'm sure there is a better way of doing this but... Can't think of it right now
    Future.successful(seq.map(_.header.status).count(_ == 200) == seq.length)
  }

  def clearDown(): Action[AnyContent] = Action.async {
    implicit request => {

      val clearRegisteredUserRepository = clearRegistration
      val clearSubscribedUserRepository = clearSubscription
      val clearEnrolmentSubscribeRepository = clearEnrolmentSubscription
      val clearEnrolmentIssuerRepository = clearEnrolmentIssuer

      for {
        clearedRegistered <- clearRegisteredUserRepository
        clearedSubscribed <- clearSubscribedUserRepository
        clearedEnrolmentSubscribed <- clearEnrolmentSubscribeRepository
        clearedEnrolmentIssuer <- clearEnrolmentIssuerRepository
        checkClearDownResult <- checkForFailed(Seq(clearedRegistered, clearedSubscribed, clearedEnrolmentSubscribed, clearedEnrolmentIssuer))
      } yield Ok(checkClearDownResult.toString)
    }
  }
}

