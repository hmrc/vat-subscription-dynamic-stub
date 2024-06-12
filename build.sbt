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

import play.sbt.routes.RoutesKeys
import sbt.*
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.*

val appName = "vat-subscription-dynamic-stub"
val hmrcMongoVersion = "2.0.0"
val bootstrapVersion = "8.5.0"

val compile: Seq[ModuleID] = Seq(ws,
  "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-30"        % hmrcMongoVersion,
  "uk.gov.hmrc"        %% "bootstrap-backend-play-30" % bootstrapVersion,
  "com.github.fge"     %  "json-schema-validator"     % "2.2.14",
  "com.github.bjansen" %  "swagger-schema-validator"  % "1.0.0"
)

def test(scope: String = "test"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"            %% "bootstrap-test-play-30"   % bootstrapVersion  % scope,
  "org.scalamock"          %% "scalamock"                % "6.0.0"           % scope,
  "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30"  % hmrcMongoVersion  % scope
)

lazy val appDependencies: Seq[ModuleID] = compile ++ test()

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;" +
      ".*Reverse.*;" +
      ".*Routes.*;" +
      "config.*;" +
      "app.*;" +
      "prod.*;" +
      "config.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 95,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(scalaSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    RoutesKeys.routesImport := Seq.empty
  )
  .settings(PlayKeys.playDefaultPort := 9156)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())

