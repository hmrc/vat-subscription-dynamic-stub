import sbt._
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.SbtAutoBuildPlugin
import play.core.PlayVersion
import sbt.Tests.{Group, SubProcess}

val appName = "manage-vat-subscription-dynamic-stub"

val compile: Seq[ModuleID] = Seq(
  "uk.gov.hmrc" %% "play-reactivemongo" % "6.2.0",
  ws,
  "uk.gov.hmrc" %% "bootstrap-play-25" % "1.7.0",
  "uk.gov.hmrc" %% "play-url-binders" % "2.1.0",
  "com.github.fge" % "json-schema-validator" % "2.2.6"
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % scope,
  "org.scalatest" %% "scalatest" % "2.2.6" % scope,
  "org.pegdown" % "pegdown" % "1.6.0" % scope,
  "org.jsoup" % "jsoup" % "1.8.3" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
  "org.mockito" % "mockito-core" % "2.7.17" % "test"
)

lazy val appDependencies : Seq[ModuleID] = compile ++ test()
lazy val plugins : Seq[Plugins] = Seq.empty
lazy val playSettings : Seq[Setting[_]] = Seq.empty

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;models/.data/..*;" +
      "filters.*;.handlers.*;components.*;.*BuildInfo.*;.*FrontendAuditConnector.*;.*Routes.*;views.html.templates.*;views.html.feedback.*;config.*;" +
      "controllers.feedback.*;app.*;prod.*;config.*;com.*;testOnly.*;\"",
    ScoverageKeys.coverageMinimum := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin) ++ plugins : _*)
  .settings(playSettings : _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    routesGenerator := StaticRoutesGenerator
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest)(base => Seq(base / "it")),
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo
  ))

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
  tests map {
    test => new Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }
