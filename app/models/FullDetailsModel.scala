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

package models

import org.apache.commons.lang3.RandomStringUtils
import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.Logger

case class FullDetailsModel(firstName: String,
                            lastName: String,
                            addressLineOne: String,
                            addressLineTwo: String,
                            townOrCity: Option[String],
                            county: Option[String],
                            postCode: Option[String],
                            country: String)

object FullDetailsModel {
  implicit val formats: OFormat[FullDetailsModel] = Json.format[FullDetailsModel]

  def getUniqueAckNo: String = {
    val length = 32
    val nanoTime = System.nanoTime()
    val restChars = length - nanoTime.toString.length
    val randomChars = RandomStringUtils.randomAlphanumeric(restChars)
    randomChars + nanoTime
  }

  val asModel: JsValue => FullDetailsModel = json => FullDetailsModel(
    firstName = (json \ "individual" \ "firstName").as[String],
    lastName = (json \ "individual" \ "lastName").as[String],
    addressLineOne = (json \ "individual" \ "addressLine1").as[String],
    addressLineTwo = (json \ "individual" \ "addressLine2").as[String],
    townOrCity =  (json \ "address" \ "addressLine3").asOpt[String],
    county = (json \ "address" \ "addressLine4").asOpt[String],
    postCode =  (json \ "address" \ "postalCode").asOpt[String],
    country = (json \ "address" \ "countryCode").as[String]
  )

  implicit val asJson: FullDetailsModel => JsValue = model => {
    Json.obj(
      "acknowledgementReference" -> getUniqueAckNo,
      "isAnAgent" -> false,
      "isAGroup" -> false,
      "individual" -> Json.obj(
        "firstName" -> model.firstName,
        "lastName" -> model.lastName
      ),
      "address" -> Json.obj(
        "addressLine1" -> model.addressLineOne,
        "addressLine2" -> model.addressLineTwo,
        "addressLine3" -> model.townOrCity,
        "addressLine4" -> model.county,
        "postalCode" -> {
          if (model.country == "GB") Some(model.postCode.getOrElse{
            Logger.warn("Attempted to submit UK address without a postcode.")
            throw new Exception("Attempted to submit UK address without a postcode.")
          })
          else model.postCode
        },
        "countryCode" -> model.country
      ),
      "contactDetails" -> Json.obj()
    )
  }
}
