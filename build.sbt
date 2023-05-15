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

import sbt._
import uk.gov.hmrc.DefaultBuildSettings._
import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}

val appName = "vat-subscription-dynamic-stub"
val hmrcMongoVersion = "1.2.0"

val compile: Seq[ModuleID] = Seq(ws,
  "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-28"        % hmrcMongoVersion,
  "uk.gov.hmrc"        %% "bootstrap-backend-play-28" % "7.15.0",
  "com.github.fge"     %  "json-schema-validator"     % "2.2.14",
  "com.github.bjansen" %  "swagger-schema-validator"  % "1.0.0"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"            %% "bootstrap-test-play-28"   % "7.15.0"          % scope,
  "org.scalamock"          %% "scalamock"                % "5.2.0"           % scope,
  "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28"  % hmrcMongoVersion  % scope
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

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(scalaSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.13.8",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    RoutesKeys.routesImport := Seq.empty
  )
  .settings(PlayKeys.playDefaultPort := 9156)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / Keys.fork := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory) (base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / testGrouping := oneForkedJvmPerTest((IntegrationTest / definedTests).value),
    IntegrationTest / parallelExecution := false
  )

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = {
  tests.map { test =>
    Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"- Dtest.name=${test.name}"))))
  }
}
