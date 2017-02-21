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

package common

object RouteIds {
  val registerIndividualWithNino = Some("register-individual-with-nino")  //RegistrationController.registerBusinessPartner
  val registerIndividualWithoutNino = Some("register-individual-without-nino")  //RegistrationController.registerBusinessPartner
  val companySubscribe = Some("subscribe-company") //CompanySubscriptionController.subscribe
  val registerAgent = Some("register-agent")  //RegistrationController.registerAgent
  val subscribe = Some("subscribe") //SubscriptionController.subscribe
  val taxEnrolmentSubscribe = Some("tax-enrolment-subscribe") //TaxEnrolmentsController.subscribe
  val taxEnrolmentIssuer = Some("tax-enrolment-issuer") //TaxEnrolmentsController.issuer
  val obtainDetails = Some("obtain-details") //RegistrationController.getExistingSAP
  val getExistingSap = Some("get-existing-sap") //RegistrationController.getExistingSap
}
