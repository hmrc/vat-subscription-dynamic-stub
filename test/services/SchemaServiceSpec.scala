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

package services

import testUtils.TestSupport
import models.SchemaModel
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.SchemaRepository
import testUtils.TestAssets.schemaModel
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

class SchemaServiceSpec extends TestSupport with DefaultPlayMongoRepositorySupport[SchemaModel] {

  override lazy val repository = new SchemaRepository(mongoComponent)
  lazy val service = new SchemaService(mongoComponent)

  "SchemaService" should {

    "find one document in the collection" in {
      val result = {
        await(service.addEntry(schemaModel))
        await(service.findById("testSchema"))
      }
      result shouldBe schemaModel
    }

    "remove one document from the collection" in {
      val result = {
        await(service.addEntry(schemaModel))
        await(service.removeById("testSchema"))
      }
      result shouldBe successDeleteResult
    }

    "remove all documents from the collection" in {
      val result = {
        await(service.addEntry(schemaModel))
        await(service.removeAll())
      }
      result shouldBe successDeleteResult
    }

    "insert a given document" in {
      val result = await(service.addEntry(schemaModel))
      result.wasAcknowledged() shouldBe true
    }

    "contain a SchemaRepository" in {
      service.repository.getClass shouldBe repository.getClass
    }
  }
}
