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

package mocks

import models.DataModel
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import org.scalamock.handlers.{CallHandler0, CallHandler1}
import org.scalamock.scalatest.MockFactory
import services.DataService
import testUtils.TestSupport

import scala.concurrent.Future

trait MockDataService extends TestSupport with MockFactory {

  lazy val mockDataService: DataService = mock[DataService]

  def mockFind(response: List[DataModel]): CallHandler1[Seq[(String, String)], Future[Seq[DataModel]]] =
    (mockDataService.find(_: Seq[(String, String)]))
      .expects(*)
      .returning(Future.successful(response))

  def mockRemoveById(response: DeleteResult): CallHandler1[String, Future[DeleteResult]] =
    (mockDataService.removeById(_: String))
      .expects(*)
      .returning(Future.successful(response))

  def mockRemoveAll(response: DeleteResult): CallHandler0[Future[DeleteResult]] =
    (() => mockDataService.removeAll())
      .expects()
      .returning(Future.successful(response))

  def mockAddEntry(response: InsertOneResult): CallHandler1[DataModel, Future[InsertOneResult]] =
    (mockDataService.addEntry(_: DataModel))
      .expects(*)
      .returning(Future.successful(response))

}
