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

import play.api.libs.json.{Json, OFormat}

case class AgentSubmissionModel (sap: String,
                                 arn: String) {
  require(AgentSubmissionModel.validateARN(arn), s"ARN:$arn is not valid.")
  require(AgentSubmissionModel.validateSAP(sap), s"SAP:$sap is not valid.")
}

object AgentSubmissionModel {
  implicit val formats: OFormat[AgentSubmissionModel] = Json.format[AgentSubmissionModel]

  def validateARN(arn: String): Boolean = {
    val regex = "[A-Z]ARN[0-9]{7}".r

    arn match {
      case regex(_*) => true
      case _ => false
    }
  }

  def validateSAP(sap: String): Boolean = {
    sap.length.equals(15)
  }
}