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

import javax.inject.Inject

import models.{AgentClientSubmissionModel, RelationshipModel, SubscriberModel}
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._

import scala.concurrent.{ExecutionContext, Future}

class DesAgentClientRelationshipRepository @Inject()() extends MongoDbConnection {

  lazy val repository = new DesAgentClientRelationshipRepositoryBase()  {

    override def findAllVersionsBy(o: String)(implicit ec: ExecutionContext): Future[Map[String, List[RelationshipModel]]] = {
      find("arn" -> o).map {
        allSubscriptions =>
          allSubscriptions.groupBy(_.arn)
      }
    }

    override def findLatestVersionBy(o: String)(implicit ec: ExecutionContext): Future[List[RelationshipModel]] = {
      findAllVersionsBy(o).map {
        _.values.toList.map {
          _.head
        }
      }
    }

    override def removeBy(o: String)(implicit ec: ExecutionContext): Future[Unit] = {
      remove("arn" -> o).map { _ => }
    }

    override def removeAll()(implicit ec: ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map { _ => }
    }

    override def addEntry(t: RelationshipModel)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map { _ => }
    }

    override def addEntries(entries: Seq[RelationshipModel])(implicit ec: ExecutionContext): Future[Unit] = {
      entries.foreach {
        addEntry
      }
      Future.successful({})
    }
  }

  def apply(): CgtRepository[RelationshipModel, String] = repository
}
