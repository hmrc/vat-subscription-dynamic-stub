/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import play.core.PlayVersion
import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}

val appName = "vat-subscription-dynamic-stub"

val compile: Seq[ModuleID] = Seq(ws,
  "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-28"        % "0.73.0",
  "uk.gov.hmrc"        %% "bootstrap-backend-play-28" % "7.4.0",
  "com.github.fge"     %  "json-schema-validator"     % "2.2.14",
  "com.github.bjansen" %  "swagger-schema-validator"  % "1.0.0"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "org.scalatest"          %% "scalatest"                 % "3.3.0-SNAP3"       % scope,
  "org.pegdown"            %  "pegdown"                   % "1.6.0"             % scope,
  "com.typesafe.play"      %% "play-test"                 % PlayVersion.current % scope,
  "org.scalatestplus.play" %% "scalatestplus-play"        % "5.1.0"             % scope,
  "org.scalamock"           %% "scalamock-scalatest-support" % "3.6.0"          % scope,
  "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"     % "0.73.0"         % scope,
  "com.vladsch.flexmark"   %  "flexmark-all"          % "0.36.8"            % scope
)

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

RoutesKeys.routesImport := Seq.empty

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
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.16",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true
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
