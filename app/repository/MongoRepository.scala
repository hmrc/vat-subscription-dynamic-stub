package repository

import play.api.libs.json.Json
import reactivemongo.api.DB
import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.mongo.{ReactiveRepository, Repository}

import scala.concurrent.{ExecutionContext, Future}

trait CGTMongoConnector[T, O] extends CGTMongoRepository {
  def apply: CGTMongoRepository
}

trait CGTRepository[T, O] extends Repository[T, BSONObjectID] {

  def findAllVersionsBy(o: O)(implicit ec: ExecutionContext): Future[Map[Long, List[T]]]

  def findLatestVersionBy(o: O)(implicit ec: ExecutionContext): Future[List[T]]

  def removeBy(o: O)(implicit ec: ExecutionContext): Future[Unit]

  def removeAll()(implicit ec: ExecutionContext): Future[Unit]

  def addEntry(t: T)(implicit ec: ExecutionContext): Future[Unit]

  def addEntries(entries: Seq[T])(implicit ec: ExecutionContext): Future[Unit]

}

abstract class CGTMongoRepository[T, O](implicit mongo: () => DB)
  extends ReactiveRepository[T, BSONObjectID]("protections", mongo, Json.format[T])
  with CGTRepository[T, O]

