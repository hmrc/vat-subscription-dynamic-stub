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

import models.{AgentClientSubmissionModel, RelationshipModel}
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands.WriteConcern

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AgentClientRelationshipRepository @Inject()() extends MongoDbConnection {

  lazy val repository = new AgentClientRelationshipRepositoryBase {

    override def findAllVersionsBy(o: RelationshipModel)(implicit ec: ExecutionContext):
    Future[Map[RelationshipModel, List[AgentClientSubmissionModel]]] = {
      val allEntries = find("arn" -> o.agentReferenceNumber)
      allEntries.map {
        _.groupBy(_.relationshipModel)
      }
    }

    override def findLatestVersionBy(o: RelationshipModel)(implicit ec: ExecutionContext): Future[List[AgentClientSubmissionModel]] = {
      val allVersions = findAllVersionsBy(o)
      allVersions.map {
        _.values.toList.map {
          _.head
        }
      }
    }

    override def removeBy(o: RelationshipModel)(implicit ec: ExecutionContext): Future[Unit] = {
      remove("arn" -> o.agentReferenceNumber).map { _ => }
    }

    override def removeAll()(implicit ec: ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map { _ => }
    }

    override def addEntry(t: AgentClientSubmissionModel)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map { _ => }
    }

    override def addEntries(entries: Seq[AgentClientSubmissionModel])(implicit ec: ExecutionContext): Future[Unit] = {
      entries.foreach {
        agentClientRelationshipIssuer =>
          insert(agentClientRelationshipIssuer)
      }
      Future.successful({})
    }
  }

  def apply(): CgtRepository[AgentClientSubmissionModel, RelationshipModel] = repository
}
