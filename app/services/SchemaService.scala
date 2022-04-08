/*
 * Copyright 2022 HM Revenue & Customs
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

package services

import models.SchemaModel
import org.mongodb.scala.model.Filters.{empty, equal}
import org.mongodb.scala.result.{InsertOneResult, DeleteResult}
import javax.inject.Inject
import repositories.SchemaRepository
import uk.gov.hmrc.mongo.MongoComponent
import scala.concurrent.{ExecutionContext, Future}

class SchemaService @Inject()(mongoComponent: MongoComponent)(implicit ec: ExecutionContext) {

  private[services] lazy val repository: SchemaRepository = new SchemaRepository(mongoComponent)

  def findById(schemaId: String): Future[SchemaModel] =
    repository.collection.find(equal("_id", schemaId)).toFuture().map(_.last)

  def removeById(schemaId: String): Future[DeleteResult] =
    repository.collection.deleteOne(equal("_id", schemaId)).toFuture()

  def removeAll(): Future[DeleteResult] =
    repository.collection.deleteMany(empty()).toFuture()

  def addEntry(document: SchemaModel): Future[InsertOneResult] =
    repository.collection.insertOne(document).toFuture()

}
