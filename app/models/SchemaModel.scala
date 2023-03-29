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

package models

import play.api.libs.json.{Format, JsNull, JsObject, JsValue, Json, Writes}
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

case class SchemaModel(_id: String,
                       url: String,
                       method: String,
                       responseSchema: JsValue,
                       requestSchema: Option[JsValue] = None,
                       schemaType: Option[String] = None)

object SchemaModel {

  implicit val dateFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  implicit val formats: Format[SchemaModel] = Format(
    Json.reads[SchemaModel],
    Writes[SchemaModel] { model =>
      JsObject(Json.obj(
        "_id" -> model._id,
        "url" -> model.url,
        "method" -> model.method,
        "responseSchema" -> model.responseSchema,
        "requestSchema" -> model.requestSchema,
        "schemaType" -> model.schemaType,
        "creationTimestamp" -> Instant.now()
      ).fields.filterNot(_._2 == JsNull))
    }
  )
}
