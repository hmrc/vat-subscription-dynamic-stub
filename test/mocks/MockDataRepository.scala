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

package mocks

import models.DataModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import reactivemongo.api.commands.{DefaultWriteResult, WriteError, WriteResult}
import repositories.{DataRepository, StubbedDataRepositoryBase}
import testUtils.TestSupport

import scala.concurrent.Future

trait MockDataRepository extends TestSupport{

  val successWriteResult = DefaultWriteResult(ok = true, n = 1, writeErrors = Seq(), None, None, None)
  val errorWriteResult = DefaultWriteResult(ok = false, n = 1, writeErrors = Seq(WriteError(1,1,"Error")), None, None, None)

  lazy val mockDataRepository: DataRepository = new DataRepository {
    override lazy val repository: StubbedDataRepositoryBase = mock[StubbedDataRepositoryBase]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockDataRepository.repository)
  }

  def mockAddEntry(document: DataModel)(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    when(mockDataRepository.repository.addEntry(ArgumentMatchers.eq(document))(ArgumentMatchers.any())).thenReturn(Future.successful(response))
  }

  def mockRemoveById(url: String)(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    when(mockDataRepository.repository.removeById(ArgumentMatchers.eq(url))(ArgumentMatchers.any())).thenReturn(Future.successful(response))
  }

  def mockRemoveAll()(response: WriteResult): OngoingStubbing[Future[WriteResult]] = {
    when(mockDataRepository.repository.removeAll()(ArgumentMatchers.any())).thenReturn(Future.successful(response))
  }

  def mockFind(response: List[DataModel]): OngoingStubbing[Future[List[DataModel]]] = {
    when(mockDataRepository.repository.find(ArgumentMatchers.any())(ArgumentMatchers.any())).thenReturn(response)
  }

}
