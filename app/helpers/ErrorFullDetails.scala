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

package helpers

import models.FullDetailsModel

case class ErrorFullDetails(fullDetails: FullDetailsModel)

object ErrorFullDetails {
  val notFound = ErrorFullDetails(FullDetailsModel("John", "Smith", "25 Big House", None, "Telford", None, "ABC 404", "UK"))
  val badRequest = ErrorFullDetails(FullDetailsModel("Bruce", "Wayne", "25 Big House", None, "Telford", None, "ABC 400", "UK"))
  val internalServerError = ErrorFullDetails(FullDetailsModel("Dr", "Who", "25 Big House", None, "Telford", None, "ABC 500", "UK"))
  val badGateway = ErrorFullDetails(FullDetailsModel("Peter", "Parker", "25 Big House", None, "Telford", None, "ABC 502", "UK"))
  val serviceUnavailable = ErrorFullDetails(FullDetailsModel("Bruce", "Banner", "25 Big House", None, "Telford", None, "ABC 503", "UK"))
  val timeout = ErrorFullDetails(FullDetailsModel("Clark", "Kent", "25 Big House", None, "Telford", None, "ABC 408", "UK"))
}
