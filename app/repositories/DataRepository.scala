/*
 * Copyright 2018 HM Revenue & Customs
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

import models.DataModel
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()() extends MongoDbConnection {

  lazy val repository = new StubbedDataRepositoryBase() {

    override def removeAll()(implicit ec: ExecutionContext): Future[WriteResult] = removeAll(WriteConcern.Acknowledged)

    override def removeById(url: String)(implicit ec: ExecutionContext): Future[WriteResult] = remove("_id" -> url)

    override def addEntry(document: DataModel)(implicit ec: ExecutionContext): Future[WriteResult] = insert(document)

    override def findById(url: String)(implicit ec: ExecutionContext): Future[DataModel] = find("_id" -> url).map(_.last)
  }

  def apply(): DynamicStubRepository[DataModel, String] = repository
}
