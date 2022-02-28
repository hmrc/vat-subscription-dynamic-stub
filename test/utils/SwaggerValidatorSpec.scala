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

package utils

import com.github.bjansen.ssv.SwaggerValidator
import com.github.fge.jsonschema.core.report.ProcessingReport
import play.api.libs.json.{JsObject, Json}
import testUtils.TestAssets.{validYamlData, yamlSchema}

import java.io.{Reader, StringReader}
import testUtils.TestSupport

class SwaggerValidatorSpec extends TestSupport {

  val spec: Reader = new StringReader(yamlSchema())
  val validator: SwaggerValidator = SwaggerValidator.forYamlSchema(spec)

  "The SwaggerValidator" should {

    "produce a successful report when the data matches the schema" in {
      val report: ProcessingReport = validator.validate(validYamlData.toString(), "/components/schemas/successResponse")
      report.isSuccess shouldBe true
    }

    "produce a failed report" when {

      "there is a required field missing" in {
        val json: JsObject = Json.obj(
          "taxPayerDetails" -> Json.obj(
            "idType" -> "VRN",
            "regimeType" -> "VATC"
          ))
        val report: ProcessingReport = validator.validate(json.toString(), "/components/schemas/successResponse")
        report.isSuccess shouldBe false
      }

      "there is a field in the wrong format" in {
        val json: JsObject = Json.obj(
          "taxPayerDetails" -> Json.obj(
            "idType" -> "VRN",
            "idNumber" -> "123456789",
            "regimeType" -> false
          ))
        val report: ProcessingReport = validator.validate(json.toString(), "/components/schemas/successResponse")
        report.isSuccess shouldBe false
      }

      "there is an additional unrecognised field" in {
        val json: JsObject = Json.obj(
          "taxPayerDetails" -> Json.obj(
            "idType" -> "VRN",
            "idNumber" -> "123456789",
            "regimeType" -> "VATC",
            "newField" -> "hooray"
          ))
        val report: ProcessingReport = validator.validate(json.toString(), "/components/schemas/successResponse")
        report.isSuccess shouldBe false
      }

      "the data is empty" in {
        val json: JsObject = Json.obj()
        val report: ProcessingReport = validator.validate(json.toString(), "/components/schemas/successResponse")
        report.isSuccess shouldBe false
      }
    }
  }
}
