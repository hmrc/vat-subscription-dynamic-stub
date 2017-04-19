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

package controllers.stubs

import actions.ExceptionTriggersActions
import common.RouteIds
import helpers.CgtRefHelper
import models._
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{CgtRepository, RouteExceptionRepository, SchemaRepository, SubscriptionRepository}
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import utils.{SchemaValidation, TestSchemas}

import scala.concurrent.Future

class SubscriptionControllerSpec extends UnitSpec with MockitoSugar with WithFakeApplication {

  def setupController(findLatestVersionResult: List[SubscriberModel],
                      ref: String,
                      expectedExceptionCode: Option[Int] = None): SubscriptionController = {

    val mockCollection = mock[CgtRepository[SubscriberModel, String]]
    val mockRepository = mock[SubscriptionRepository]
    val mockCGTRefHelper = mock[CgtRefHelper]
    val mockExceptionsCollection = mock[CgtRepository[RouteExceptionModel, RouteExceptionKeyModel]]
    val mockExceptionsRepository = mock[RouteExceptionRepository]
    val mockSchemaRepository = mock[SchemaRepository]
    val mockSchemaCollection = mock[CgtRepository[SchemaModel, String]]
    val exceptionTriggersActions = new ExceptionTriggersActions(mockExceptionsRepository)
    val expectedException = expectedExceptionCode.fold(List[RouteExceptionModel]()) {
      code => List(RouteExceptionModel("", "", code))
    }

    when(mockExceptionsRepository.apply())
      .thenReturn(mockExceptionsCollection)

    when(mockExceptionsCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(expectedException))

    when(mockRepository.apply())
      .thenReturn(mockCollection)

    when(mockCollection.addEntry(any())(any()))
      .thenReturn(Future.successful({}))

    when(mockCollection.findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(findLatestVersionResult))

    when(mockCGTRefHelper.generateCGTReference())
      .thenReturn(ref)

    when(mockSchemaRepository.apply())
      .thenReturn(mockSchemaCollection)

    when(mockSchemaRepository().findLatestVersionBy(any())(any()))
      .thenReturn(Future.successful(List(SchemaModel(RouteIds.subscribe, TestSchemas.subscriptionCreateIndvOrgSchema))))

    val schemaValidation = new SchemaValidation(mockSchemaRepository)

    new SubscriptionController(mockRepository, mockCGTRefHelper, exceptionTriggersActions, schemaValidation)
  }

  "Calling subscribe" when {

    "a CGT subscription already exists" should {
      val controller = setupController(List(SubscriberModel("123456789098765", "CGT123456")), "CGT654321")
      lazy val result = controller.subscribe("123456789098765")(FakeRequest("POST", ""))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "subscriptionCGT" \ "referenceNumber").as[String] shouldBe "CGT123456"
      }
    }

    "no existing CGT subscription exists" should {
      val controller = setupController(Nil, "CGT654321")
      lazy val result = controller.subscribe("123456789098765")(FakeRequest("POST", ""))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "return a type of Json" in {
        contentType(result) shouldBe Some("application/json")
      }

      "return a valid CGT Reference" in {
        val data = contentAsString(result)
        val json = Json.parse(data)
        (json \ "subscriptionCGT" \ "referenceNumber").as[String] shouldBe "CGT654321"
      }
    }
  }
}
