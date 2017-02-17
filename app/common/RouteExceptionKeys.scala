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

object RouteExceptionKeys {
  val registerIndividual = "register-individual"  //RegistrationController.registerBusinessPartner
  val companySubscribe = "subscribe-company" //CompanySubscriptionController.subscribe
  val registerAgent = "register-agent"  //RegistrationController.registerAgent
  val registerGhost = "ghost-register-user" //GhostRegistrationController.registerBusinessPartner
  val subscribe = "subscribe" //SubscriptionController.subscribe
  val taxEnrolmentSubscribe = "tax-enrolment-subscribe" //TaxEnrolmentsController.subscribe
  val taxEnrolmentIssuer = "tax-enrolment-issuer" //TaxEnrolmentsController.issuer
  val obtainDetails = "obtain-details" //RegistrationController.getExistingSAP
}
