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

package fakeStuff

import models.DataModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import repositories.DataRepository
import uk.gov.hmrc.play.test.UnitSpec
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MockDataRepository extends UnitSpec with MockitoSugar with BeforeAndAfterEach {

  val mockDataRepository: DataRepository = mock[DataRepository]

  val mockWriteResult: WriteResult = mock[WriteResult]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataRepository)
  }

  def mockAddEntry(document: DataModel)(response: WriteResult): Unit = {
    when(mockDataRepository().addEntry(ArgumentMatchers.eq(document)))
      .thenReturn(Future.successful(response))
  }

  def writeResultOk(): Unit = {
    when(mockWriteResult.ok).thenReturn(true)
    when(mockWriteResult.n).thenReturn(1)
    when(mockWriteResult.code).thenReturn(Some(1))
  }

}
