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

package testUtils

import models.{DataModel, SchemaModel}
import play.api.libs.json.{JsObject, JsValue, Json}

object TestAssets {

  val jsonSchema: JsValue = Json.parse("""
    {
      "title": "Person",
      "type": "object",
      "properties": {
        "firstName": {
          "type": "string"
        },
        "lastName": {
          "type": "string"
        }
      },
      "required": ["firstName", "lastName"]
    }"""
  )

  //scalastyle:off
  def yamlSchema(successSchemaDefinition: String): String =
    s"""
      |openapi: 1
      |info:
      |  title: Example
      |  description: Example API
      |  version: 0.1.0
      |  contact:
      |    name: HMRC
      |    email: hmrc@gov.uk
      |servers:
      |  - url: 'https://{hostname}:{port}'
      |    description: F
      |    variables:
      |      hostname:
      |        default: 'junk'
      |        description: 'junk'
      |      port:
      |        default: '443'
      |        description: 'The port for junk'
      |tags:
      |  - name: API#1
      |    description: Get Details
      |paths:
      |  '/details/{idType}':
      |    get:
      |      summary: API#1 Get Details
      |      description: F
      |      tags:
      |        - API#1
      |      security:
      |        - bearerAuth: []
      |      parameters:
      |        - $$ref: '#/components/parameters/correlationId'
      |        - $$ref: '#/components/parameters/idTypePathParam'
      |        - $$ref: '#/components/parameters/onlyOpenItemsQueryParam'
      |      responses:
      |        '200':
      |          description: Successful Response
      |          headers:
      |            CorrelationId:
      |              $$ref: '#/components/headers/CorrelationId'
      |          content:
      |            application/json;charset=UTF-8:
      |              schema:
      |                $$ref: '#/components/schemas/$successSchemaDefinition'
      |              examples:
      |                Success Response:
      |                  value:
      |                    taxPayerDetails:
      |                      idType: VRN
      |                      idNumber: 123456789
      |                      regimeType: VATC
      |        '400':
      |          description: |-
      |            Bad request
      |            ```
      |            A bad request has been made, this could be due to one or more issues with the request
      |            "code"                        "reason"
      |            INVALID_CORRELATIONID         Submission has not passed validation. Invalid header CorrelationId.
      |            INVALID_IDTYPE                Submission has not passed validation. Invalid parameter idType.
      |          headers:
      |            CorrelationId:
      |              $$ref: '#/components/headers/CorrelationId'
      |          content:
      |            application/json;charset=UTF-8:
      |              schema:
      |                $$ref: '#/components/schemas/failureResponse'
      |              examples:
      |                Error_InvalidCorrelationId:
      |                  value:
      |                    failures:
      |                      - code: INVALID_CORRELATIONID
      |                        reason: Submission has not passed validation. Invalid header CorrelationId.
      |                Error_Invalid_idtype:
      |                  value:
      |                    failures:
      |                      - code: INVALID_IDTYPE
      |                        reason: Submission has not passed validation. Invalid parameter idType.
      |        '404':
      |          description: |-
      |            Not Found
      |             ```
      |             Error at backend, this could be due to one or more issues with the request
      |              "code"                  "reason"
      |            NO_DATA_FOUND   The remote endpoint has indicated that no data can be found.
      |          headers:
      |            CorrelationId:
      |              $$ref: '#/components/headers/CorrelationId'
      |          content:
      |            application/json;charset=UTF-8:
      |              schema:
      |                $$ref: '#/components/schemas/failureResponse'
      |              examples:
      |                Error_NotFound:
      |                  value:
      |                    failures:
      |                      - code: NO_DATA_FOUND
      |                        reason: The remote endpoint has indicated that no data can be found.
      |        '500':
      |          description: |-
      |            Server Error
      |            ```
      |            "code"         "reason"
      |            SERVER_ERROR   IF is currently experiencing problems that require live service intervention.
      |          headers:
      |            CorrelationId:
      |              $$ref: '#/components/headers/CorrelationId'
      |          content:
      |            application/json;charset=UTF-8:
      |              schema:
      |                $$ref: '#/components/schemas/failureResponse'
      |              examples:
      |                ServerError:
      |                  value:
      |                    failures:
      |                      - code: SERVER_ERROR
      |                        reason: IF is currently experiencing problems that require live service intervention.
      |components:
      |  securitySchemes:
      |    bearerAuth:
      |      type: http
      |      scheme: bearer
      |  headers:
      |    CorrelationId:
      |      description: A UUID format string for the transaction used for traceability purposes
      |      schema:
      |        type: string
      |        format: uuid
      |  parameters:
      |    idTypePathParam:
      |      in: path
      |      name: idType
      |      description: 'Allowed values: MTDBSA, NINO'
      |      required: true
      |      schema:
      |        type: string
      |        pattern: '^[A-Za-z]{1,6}$$'
      |    onlyOpenItemsQueryParam:
      |      in: query
      |      name: onlyOpenItems
      |      description: 'Limits the extraction to unpaid or not reversed charges.'
      |      required: true
      |      schema:
      |        type: boolean
      |  schemas:
      |    failureResponse:
      |      type: object
      |      additionalProperties: false
      |      required:
      |        - failures
      |      properties:
      |        failures:
      |          type: array
      |          minItems: 1
      |          uniqueItems: true
      |          items:
      |            type: object
      |            additionalProperties: false
      |            required:
      |              - code
      |              - reason
      |            properties:
      |              code:
      |                description: Keys for all the errors returned
      |                type: string
      |                pattern: '^[A-Z0-9_-]{1,160}$$'
      |              reason:
      |                description: A simple description for the failure
      |                type: string
      |                minLength: 1
      |                maxLength: 160
      |    $successSchemaDefinition:
      |      description: Get Details Response
      |      type: object
      |      properties:
      |        taxPayerDetails:
      |          type: object
      |          properties:
      |            idType:
      |              description: 'Expected values: MTDBSA , NINO'
      |              type: string
      |              pattern: '^[A-Z]{1,10}$$'
      |            idNumber:
      |              description: 'MTDBSA number, Example XQIT00000000001. NINO number, Example IN408059B'
      |              type: string
      |              pattern: '^[0-9A-Z]{1,30}$$'
      |            regimeType:
      |              description: 'RegimeType, expected value ITSA'
      |              type: string
      |              pattern: '^[A-Z]{1,10}$$'
      |          required:
      |            - idType
      |            - idNumber
      |            - regimeType
      |          additionalProperties: false
      |      required:
      |      - taxPayerDetails
      |      additionalProperties: false
      |""".stripMargin

  val validYamlData: JsObject = Json.obj(
    "taxPayerDetails" -> Json.obj(
      "idType" -> "VRN",
      "idNumber" -> "123456789",
      "regimeType" -> "VATC"
    ))

  val validYamlFailureData: JsObject = Json.obj(
    "failures" -> Json.arr(Json.obj(
      "code" -> "A_CODE",
      "reason" -> "A reason."
    ))
  )

  val schemaModel: SchemaModel = SchemaModel("testSchema", "/test", "GET", responseSchema = jsonSchema)

  val dataModel: DataModel = DataModel("/test", "testSchema", "GET", 200, response = Some(Json.parse("{}")) )
}
