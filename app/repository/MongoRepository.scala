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

import play.api.libs.json.Format
import reactivemongo.api.DB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.{ExecutionContext, Future}

trait CGTRepository[T, O] extends Repository[T, BSONObjectID] {

  def findAllVersionsBy(o: O)(implicit ec: ExecutionContext): Future[Map[O, List[T]]]

  def findLatestVersionBy(o: O)(implicit ec: ExecutionContext): Future[List[T]]

  def removeBy(o: O)(implicit ec: ExecutionContext): Future[Unit]

  def removeAll()(implicit ec: ExecutionContext): Future[Unit]

  def addEntry(t: T)(implicit ec: ExecutionContext): Future[Unit]

  def addEntries(entries: Seq[T])(implicit ec: ExecutionContext): Future[Unit]

}

abstract class BPMongoRepository[T, O](implicit mongo: () => DB, formats: Format[T], manifest: Manifest[T])
  extends ReactiveRepository[T, BSONObjectID]("businessPartners", mongo, formats)
    with CGTRepository[T, O]

abstract class SubscriberMongoRepository[T, O](implicit mongo: () => DB, formats: Format[T], manifest: Manifest[T])
  extends ReactiveRepository[T, BSONObjectID]("subscribers", mongo, formats)
    with CGTRepository[T, O]

abstract class EnrolmentMongoRepository[T, O](implicit mongo: () => DB, formats: Format[T], manifest: Manifest[T])
  extends ReactiveRepository[T, BSONObjectID]("enrolments", mongo, formats)
    with CGTRepository[T, O]
