/*
 * Copyright 2021 HM Revenue & Customs
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
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.DefaultDB
import reactivemongo.api.commands._
import uk.gov.hmrc.mongo.MongoConnector
import reactivemongo.api.WriteConcern.Acknowledged

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SchemaRepository @Inject()(reactiveMongoComponent: ReactiveMongoComponent) {

  lazy val mongoConnector: MongoConnector = reactiveMongoComponent.mongoConnector
  implicit lazy val db: () => DefaultDB = mongoConnector.db

  lazy val repository = new SchemaRepositoryBase() {

    override def findById(schemaId: String)(implicit ec: ExecutionContext): Future[SchemaModel] =
      find("_id" -> schemaId).map(_.last)

    override def removeById(schemaId: String)(implicit ec: ExecutionContext): Future[WriteResult] =
      remove("_id" -> schemaId)

    override def removeAll()(implicit ec: ExecutionContext): Future[WriteResult] =
      removeAll(Acknowledged)

    override def addEntry(document: SchemaModel)(implicit ec: ExecutionContext): Future[WriteResult] =
      insert(document)
  }

  def apply(): DynamicStubRepository[SchemaModel, String] = repository
}
