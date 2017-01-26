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

package repository

import com.google.inject.{Inject, Singleton}
import models.{Identifier, SubscriptionIssuerRequest, SubscriptionSubscriberRequest}
import org.apache.commons.lang3.exception.ExceptionContext
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubscriptionTaxEnrolmentConnector @Inject()() extends MongoDbConnection {

  lazy val issuerRepository = new CGTMongoRepository[SubscriptionIssuerRequest, Identifier] {
    override def findAllVersionsBy(o: Identifier)(implicit ec: ExecutionContext): Future[Map[Identifier, List[SubscriptionIssuerRequest]]] = {
      val allEntries = find("identifier.nino" -> o.nino)
      allEntries.map {
        allSubscriptionIssuerRequests =>
          allSubscriptionIssuerRequests.groupBy(_.identifier)
      }
    }

    override def findLatestVersionBy(o: Identifier)(implicit ec: ExecutionContext): Future[List[SubscriptionIssuerRequest]] = {
      val allVersions = findAllVersionsBy(o)
      allVersions.map {
        _.values.toList.map {_.head}
      }
    }

    override def removeBy(o: Identifier)(implicit ec: ExecutionContext): Future[Unit] ={
      //same as above...
      remove("identifier.nino" -> o.nino).map {_ => }
    }

    override def removeAll()(implicit ec:ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map {_ => }
    }

    override def addEntry(t: SubscriptionIssuerRequest)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map {_ => }
    }

    override def addEntries(entries: Seq[SubscriptionIssuerRequest])(implicit ec: ExecutionContext): Future[Unit] ={
      entries.foreach {
        subscriptionIssuer =>
          insert(subscriptionIssuer)
      }
      Future.successful()
    }
  }

  lazy val subscriberRepository = new CGTMongoRepository[SubscriptionSubscriberRequest, String] {
    override def findAllVersionsBy(o: String)(implicit ec: ExecutionContext): Future[Map[String, List[SubscriptionSubscriberRequest]]] = {
      val allEntries = find("etmpId" -> o)
      //unsure, might need to look at the nino with-in the Identifier object
      allEntries.map {
        allSubscriptionIssuerRequests =>
          allSubscriptionIssuerRequests.groupBy(_.etmpId)
      }
    }

    override def findLatestVersionBy(o: String)(implicit ec: ExecutionContext): Future[List[SubscriptionSubscriberRequest]] = {
      val allVersions = findAllVersionsBy(o)
      allVersions.map {
        _.values.toList.map {_.head}
      }
    }

    override def removeBy(o: String)(implicit ec: ExecutionContext): Future[Unit] ={
      //same as above...
      remove("etmpId" -> o).map {_ => }
    }

    override def removeAll()(implicit ec:ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map {_ => }
    }

    override def addEntry(t: SubscriptionSubscriberRequest)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map {_ => }
    }

    override def addEntries(entries: Seq[SubscriptionSubscriberRequest])(implicit ec: ExecutionContext): Future[Unit] ={
      entries.foreach {
        subscriptionIssuer =>
          insert(subscriptionIssuer)
      }
      Future.successful()
    }
  }

}
