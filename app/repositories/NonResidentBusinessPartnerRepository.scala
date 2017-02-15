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
import models.{FullDetailsModel, NonResidentBusinessPartnerModel}
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NonResidentBusinessPartnerRepository @Inject()() extends MongoDbConnection {

  lazy val repository = new NonResidentBusinessPartnerRepositoryBase() {

    def findAllVersionsBy(o: FullDetailsModel)(implicit ec: ExecutionContext): Future[Map[FullDetailsModel, List[NonResidentBusinessPartnerModel]]] = {
      find("firstName" -> o.firstName, "lastName" -> o.lastName, "addressLineOne" -> o.addressLineOne).map { allBP =>
        allBP.groupBy(_.fullDetailsModel)
      }
    }

    def findLatestVersionBy(o: FullDetailsModel)(implicit ec: ExecutionContext): Future[List[NonResidentBusinessPartnerModel]] = {
      findAllVersionsBy(o).map {
        _.values.toList.map {
          _.head
        }
      }
    }

    def removeBy(o: FullDetailsModel)(implicit ec: ExecutionContext): Future[Unit] = {
      remove("firstName" -> o.firstName, "lastName" -> o.lastName).map { _ => }
    }

    def removeAll()(implicit ec: ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map { _ => }
    }

    def addEntry(t: NonResidentBusinessPartnerModel)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map { _ => }
    }

    def addEntries(entries: Seq[NonResidentBusinessPartnerModel])(implicit ec: ExecutionContext): Future[Unit] = {
      entries.foreach {
        addEntry
      }
      Future.successful({})
    }
  }

  def apply(): CgtRepository[NonResidentBusinessPartnerModel, FullDetailsModel] = repository

}
