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

import models.SchemaModel
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SchemaRepository @Inject()() extends MongoDbConnection {

  lazy val repository = new SchemaRepositoryBase() {

    override def removeAll()(implicit ec: ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map { _ => }
    }

    override def removeBy(schemaId: String)(implicit ec: ExecutionContext): Future[Unit] = {
      remove("schemaId" -> schemaId).map { _ => }
    }

    override def addEntry(document: SchemaModel)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(document).map { _ => }
    }

    override def addEntries(entries: Seq[SchemaModel])(implicit ec: ExecutionContext): Future[Unit] = {
      entries.foreach {
        addEntry
      }
      Future.successful({})
    }

    override def findLatestVersionBy(schemaId: String)(implicit ec: ExecutionContext): Future[List[SchemaModel]] = {
      findAllVersionsBy(schemaId).map {
        _.values.toList.map {
          _.last
        }
      }
    }

    override def findAllVersionsBy(schemaId: String)
                                  (implicit ec: ExecutionContext): Future[Map[String, List[SchemaModel]]] = {
      find("_id" -> schemaId).map {
        schemas =>
          schemas.groupBy(_._id)
      }
    }
  }

  def apply(): DynamicStubRepository[SchemaModel, String] = repository
}
