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
import models.BusinessPartner
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.commands._
import uk.gov.hmrc.domain.Nino

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class BPMongoConnector @Inject()() extends MongoDbConnection {

  lazy val repository = new CGTMongoRepository[BusinessPartner, Nino]() {

    def findAllVersionsBy(o: Nino)(implicit ec: ExecutionContext): Future[Map[Nino, List[BusinessPartner]]] = {
      val allEntries = find("nino" -> o.nino)
      allEntries.map { allBP =>
        allBP.groupBy(_.nino)
      }
    }

    def findLatestVersionBy(o: Nino)(implicit ec: ExecutionContext): Future[List[BusinessPartner]] = {
      val allVersions = findAllVersionsBy(o)
      allVersions.map {
        _.values.toList.map {_.head}
      }
    }

    def removeBy(o: Nino)(implicit ec: ExecutionContext): Future[Unit] = {
      remove("nino" -> o.nino).map {_ => }
    }

    def removeAll()(implicit ec: ExecutionContext): Future[Unit] = {
      removeAll(WriteConcern.Acknowledged).map {_ => }
    }

    def addEntry(t: BusinessPartner)(implicit ec: ExecutionContext): Future[Unit] = {
      insert(t).map {_ => }
    }

    def addEntries(entries: Seq[BusinessPartner])(implicit ec: ExecutionContext): Future[Unit] = {
      entries.foreach { partner =>
        insert(partner)
      }
      Future.successful()
    }
  }

  def apply(): CGTMongoRepository[BusinessPartner, Nino] = repository

}
