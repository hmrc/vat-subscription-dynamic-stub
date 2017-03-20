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

package repositories

import models._
import play.api.libs.json.Format
import reactivemongo.api.DB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.{ExecutionContext, Future}

trait CgtRepository[T, O] extends Repository[T, BSONObjectID] {

  def findAllVersionsBy(o: O)(implicit ec: ExecutionContext): Future[Map[O, List[T]]]

  def findLatestVersionBy(o: O)(implicit ec: ExecutionContext): Future[List[T]]

  def removeBy(o: O)(implicit ec: ExecutionContext): Future[Unit]

  def removeAll()(implicit ec: ExecutionContext): Future[Unit]

  def addEntry(t: T)(implicit ec: ExecutionContext): Future[Unit]

  def addEntries(entries: Seq[T])(implicit ec: ExecutionContext): Future[Unit]

}

abstract class BusinessPartnerRepositoryBase
(implicit mongo: () => DB, formats: Format[BusinessPartnerModel], manifest: Manifest[BusinessPartnerModel])
  extends ReactiveRepository[BusinessPartnerModel, BSONObjectID]("businessPartners", mongo, formats)
    with CgtRepository[BusinessPartnerModel, Nino]

abstract class SubscriberRepositoryBase
(implicit mongo: () => DB, formats: Format[SubscriberModel], manifest: Manifest[SubscriberModel])
  extends ReactiveRepository[SubscriberModel, BSONObjectID]("subscribers", mongo, formats)
    with CgtRepository[SubscriberModel, String]

abstract class TaxEnrolmentIssuerRepositoryBase
(implicit mongo: () => DB, formats: Format[EnrolmentIssuerRequestModel], manifest: Manifest[EnrolmentIssuerRequestModel])
  extends ReactiveRepository[EnrolmentIssuerRequestModel, BSONObjectID]("enrolments", mongo, formats)
    with CgtRepository[EnrolmentIssuerRequestModel, Identifier]

abstract class TaxEnrolmentSubscriberRepositoryBase
(implicit mongo: () => DB, formats: Format[EnrolmentSubscriberRequestModel], manifest: Manifest[EnrolmentSubscriberRequestModel])
  extends ReactiveRepository[EnrolmentSubscriberRequestModel, BSONObjectID]("enrolments", mongo, formats)
    with CgtRepository[EnrolmentSubscriberRequestModel, String]

abstract class NonResidentBusinessPartnerRepositoryBase
(implicit mongo: () => DB, formats: Format[NonResidentBusinessPartnerModel], manifest: Manifest[NonResidentBusinessPartnerModel])
  extends ReactiveRepository[NonResidentBusinessPartnerModel, BSONObjectID]("nonResidentBusinessPartners", mongo, formats)
    with CgtRepository[NonResidentBusinessPartnerModel, FullDetailsModel]

abstract class AgentClientRelationshipRepositoryBase
(implicit mongo: () => DB, formats: Format[AgentClientSubmissionModel], manifest: Manifest[AgentClientSubmissionModel])
  extends ReactiveRepository[AgentClientSubmissionModel, BSONObjectID]("agentClientRelationships", mongo, formats)
    with CgtRepository[AgentClientSubmissionModel, RelationshipModel]

abstract class RouteExceptionRepositoryBase
(implicit mongo: () => DB, formats: Format[RouteExceptionModel], manifest: Manifest[RouteExceptionModel])
  extends ReactiveRepository[RouteExceptionModel, BSONObjectID]("routeExceptions", mongo, formats)
    with CgtRepository[RouteExceptionModel, RouteExceptionKeyModel]
