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

import javax.inject.{Inject, Singleton}
import models.EnrolmentSubscriberRequestModel
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaxEnrolmentSubscriberRepository @Inject()() extends MongoDbConnection {

  lazy val repository = new TaxEnrolmentSubscriberRepositoryBase {
    override def findAllVersionsBy(o: String)(implicit ec: ExecutionContext): Future[Map[String, List[EnrolmentSubscriberRequestModel]]] = {
      val allEntries = find("etmpId" -> o)
      //unsure, might need to look at the nino with-in the Identifier object
      allEntries.map {
        allSubscriptionIssuerRequests =>
          allSubscriptionIssuerRequests.groupBy(_.etmpId)
      }
    }

    override def findLatestVersionBy(o: String)(implicit ec: ExecutionContext): Future[List[EnrolmentSubscriberRequestModel]] = {
      val allVersions = findAllVersionsBy(o)
      allVersions.map {
        _.values.toList.map {
          _.head
        }
      }
    }

    override def removeBy(o: String)(implicit ec: ExecutionContext): Future[Unit] = {
      //same as above...
      remove("etmpId" -> o).map { _ => }
    }

    override def removeAll()(implicit ec: ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map { _ => }
    }

    override def addEntry(t: EnrolmentSubscriberRequestModel)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map { _ => }
    }

    override def addEntries(entries: Seq[EnrolmentSubscriberRequestModel])(implicit ec: ExecutionContext): Future[Unit] = {
      entries.foreach {
        subscriptionIssuer =>
          insert(subscriptionIssuer)
      }
      Future.successful({})
    }
  }

  def apply(): CgtRepository[EnrolmentSubscriberRequestModel, String] = repository

}
