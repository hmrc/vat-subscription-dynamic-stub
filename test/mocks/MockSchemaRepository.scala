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

import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import reactivemongo.api.commands.{UpdateWriteResult, WriteError, WriteResult}
import repositories.{SchemaRepository, SchemaRepositoryBase}
import testUtils.TestSupport

import scala.concurrent.Future

trait MockSchemaRepository extends TestSupport {

  val successWriteResult: UpdateWriteResult =
    UpdateWriteResult(ok = true, n = 1, nModified = 1, upserted = Seq(), writeErrors = Seq(), None, None, None)
  val errorWriteResult: UpdateWriteResult =
    UpdateWriteResult(ok = false, n = 1, nModified = 0, upserted = Seq(), writeErrors = Seq(WriteError(1,1,"Error")), None, None, None)

  lazy val mockSchemaRepository: SchemaRepository = new SchemaRepository(rmc) {
    override lazy val repository: SchemaRepositoryBase = mock[SchemaRepositoryBase]
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSchemaRepository.repository)
  }

  def setupMockAddSchema(response: Future[WriteResult]): OngoingStubbing[Future[WriteResult]] =
    when(mockSchemaRepository.repository.addEntry(ArgumentMatchers.any())(ArgumentMatchers.any())).thenReturn(response)

  def setupMockRemoveSchema(id: String)(response: WriteResult): OngoingStubbing[Future[WriteResult]] =
    when(mockSchemaRepository.repository.removeById(ArgumentMatchers.eq(id))(ArgumentMatchers.any())).thenReturn(Future.successful(response))

  def setupMockRemoveAllSchemas(response: WriteResult): OngoingStubbing[Future[WriteResult]] =
    when(mockSchemaRepository.repository.removeAll()(ArgumentMatchers.any())).thenReturn(Future.successful(response))

}
