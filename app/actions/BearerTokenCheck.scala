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

package actions

import play.api.mvc.{ActionBuilder, Result}
import play.mvc.Http.Request
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

class BearerTokenCheck {
  case class BearerTokenCheck(request: Request) extends ActionBuilder[Request] {
    def invokeBlock[A](request: Request[A], block: (Request[A] => Future[Result])) = {
      block(request)
    }
  }
}
