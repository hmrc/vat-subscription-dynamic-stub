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

case class ErrorSafeId (sap: String)

object ErrorSafeId {
  val notFound = ErrorSafeId("404404404")
  val badRequest = ErrorSafeId("400400400")
  val internalServerError = ErrorSafeId("500500500")
  val badGateway = ErrorSafeId("502502502")
  val serviceUnavailable = ErrorSafeId("503503503")
  val timeout = ErrorSafeId("408408408")
}

case class CompanyErrorSafeId(sap: String)

object CompanyErrorSafeId {
  val notFound = ErrorSafeId("003404404")
  val badRequest = ErrorSafeId("003400400")
  val internalServerError = ErrorSafeId("003500500")
  val badGateway = ErrorSafeId("003502502")
  val serviceUnavailable = ErrorSafeId("003503503")
  val timeout = ErrorSafeId("003408408")
}
