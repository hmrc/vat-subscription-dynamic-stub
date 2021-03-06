/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
import play.core.PlayVersion
import play.sbt.routes.RoutesKeys
import sbt.Tests.{Group, SubProcess}

val appName = "vat-subscription-dynamic-stub"

val compile: Seq[ModuleID] = Seq(ws,
  "uk.gov.hmrc"     %% "simple-reactivemongo"       % "7.31.0-play-26",
  "uk.gov.hmrc"     %% "bootstrap-backend-play-26"  % "4.1.0",
  "com.github.fge"  %  "json-schema-validator"      % "2.2.6"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc"             %% "hmrctest"           % "3.10.0-play-26"    % scope,
  "org.scalatest"           %% "scalatest"          % "3.0.9"             % scope,
  "org.pegdown"             %  "pegdown"            % "1.6.0"             % scope,
  "org.jsoup"               %  "jsoup"              % "1.13.1"            % scope,
  "com.typesafe.play"       %% "play-test"          % PlayVersion.current % scope,
  "org.scalatestplus.play"  %% "scalatestplus-play" % "3.1.3"             % scope,
  "org.mockito"             %  "mockito-core"       % "3.2.4"             % scope
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
      "models/.data/..*;" +
      "filters.*;" +
      ".handlers.*;" +
      "components.*;" +
      ".*BuildInfo.*;" +
      ".*FrontendAuditConnector.*;" +
      ".*Routes.*;" +
      "views.html.templates.*;" +
      "views.html.feedback.*;" +
      "config.*;" +
      "controllers.feedback.*;" +
      "app.*;" +
      "prod.*;" +
      "config.*;" +
      "com.*;" +
      "testOnly.*;" +
      "\"",
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.11",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)
  )
  .settings(PlayKeys.playDefaultPort := 9156)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo
  ))

def oneForkedJvmPerTest(tests: Seq[TestDefinition]): Seq[Group] = {
  tests.map { test =>
    Group(test.name, Seq(test), SubProcess(ForkOptions().withRunJVMOptions(Vector(s"- Dtest.name=${test.name}"))))
  }
}
