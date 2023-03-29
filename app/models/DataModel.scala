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

case class DataModel(_id: String,
                     schemaId: String,
                     method: String,
                     status: Int,
                     response: Option[JsValue])

object DataModel {

  implicit val dateFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  implicit val formats: Format[DataModel] = Format(
    Json.reads[DataModel],
    Writes[DataModel] { model =>
      JsObject(Json.obj(
        "_id" -> model._id,
        "schemaId" -> model.schemaId,
        "method" -> model.method,
        "status" -> model.status,
        "response" -> model.response,
        "creationTimestamp" -> Instant.now()
      ).fields.filterNot(_._2 == JsNull))
    }
  )
}
