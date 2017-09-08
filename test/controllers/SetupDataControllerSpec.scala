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

package controllers

import akka.util.ByteString
import org.scalatest.mock.MockitoSugar
import play.api.libs.json.Json
import play.mvc.Http.Status
import uk.gov.hmrc.play.http.HttpResponse
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import fakeStuff._
import models.DataModel
import play.api.libs.streams.Accumulator
import play.api.mvc.Result
import play.api.test.FakeRequest
import reactivemongo.api.commands.WriteResult

import scala.concurrent.Future
import akka.stream.Materializer
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

class SetupDataControllerSpec extends UnitSpec with MockitoSugar with MockSchemaValidation with MockDataRepository{

    val successResponse = HttpResponse(Status.OK, responseJson = Some(Json.parse("{}")))

    //def mockApp: Application = new GuiceApplicationBuilder().build()
    //val mtrlzr: Materializer = mockApp.injector.instanceOf[Materializer]

    object TestSetupDataController extends SetupDataController(mockSchemaValidation, mockDataRepository)

    "SetupDataController.addData" should {

        val dataModel: DataModel = DataModel(
            _id = "1234",
            schemaId = "2345",
            method = "GET",
            response = Some(Json.parse("""{"calcID": "12345671", "calcTimestamp": "2017-01-01T00:35:34.185Z", "calcAmount": 0}""")),
            status = Status.OK)

        lazy val result = TestSetupDataController.addData()(FakeRequest("POST", "/").withJsonBody(Json.parse("""{ "field": "value" }""")))

        "return Status OK (200)" in {
            mockValidateUrlMatch("1234", "http://something")(response = true)
            mockValidateResponseJson("1234", None)(response = true)
            mockLoadUrlRegex("1234")("regex")
            writeResultOk()
            mockAddEntry(dataModel)(response = mockWriteResult)

            //status(result.run()(mtrlzr)) shouldBe Status.OK
        }

    }

}