/*
 * Copyright 2023 HM Revenue & Customs
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

import models.SchemaModel
import org.mongodb.scala.result.{DeleteResult, InsertOneResult}
import org.scalamock.handlers.{CallHandler0, CallHandler1}
import services.SchemaService
import testUtils.TestSupport
import org.scalamock.scalatest.MockFactory

import scala.concurrent.Future

trait MockSchemaService extends TestSupport with MockFactory {

  lazy val mockSchemaService: SchemaService = mock[SchemaService]

  def mockFindById(response: Future[SchemaModel]): CallHandler1[String, Future[SchemaModel]] =
    (mockSchemaService.findById(_: String))
      .expects(*)
      .returning(response)

  def mockRemoveById(response: DeleteResult): CallHandler1[String, Future[DeleteResult]] =
    (mockSchemaService.removeById(_: String))
      .expects(*)
      .returning(Future.successful(response))

  def mockRemoveAll(response: DeleteResult): CallHandler0[Future[DeleteResult]] =
    (() => mockSchemaService.removeAll())
      .expects()
      .returning(Future.successful(response))

  def mockAddEntry(response: Future[InsertOneResult]): CallHandler1[SchemaModel, Future[InsertOneResult]] =
    (mockSchemaService.addEntry(_: SchemaModel))
      .expects(*)
      .returning(response)
}
