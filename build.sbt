/*
 * Copyright 2019 BusyMachines
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

//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("github-gen", "githubWorkflowGenerate")
addCommandAlias("github-check", "githubWorkflowCheck")
Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala213  = "2.13.5"
val Scala3RC1 = "3.0.0-RC1"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

//see: https://github.com/xerial/sbt-sonatype#buildsbt
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / baseVersion  := "0.1.0"
ThisBuild / organization := "com.busymachines"
ThisBuild / organizationName := "BusyMachines"
ThisBuild / homepage     := Option(url("https://github.com/busymachines/pureharm-testkit"))

ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl  = url("https://github.com/busymachines/pureharm-testkit"),
    connection = "git@github.com:busymachines/pureharm-testkit.git",
  )
)

/** I want my email. So I put this here. To reduce a few lines of code,
  * the sbt-spiewak plugin generates this (except email) from these two settings:
  * {{{
  * ThisBuild / publishFullName   := "Loránd Szakács"
  * ThisBuild / publishGithubUser := "lorandszakacs"
  * }}}
  */
ThisBuild / developers := List(
  Developer(
    id    = "lorandszakacs",
    name  = "Loránd Szakács",
    email = "lorand.szakacs@protonmail.com",
    url   = new java.net.URL("https://github.com/lorandszakacs"),
  )
)

ThisBuild / startYear := Some(2019)
ThisBuild / licenses   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//until we get to 1.0.0, we keep strictSemVer false
ThisBuild / strictSemVer              := false
ThisBuild / spiewakCiReleaseSnapshots := true
ThisBuild / spiewakMainBranches       := List("main")
ThisBuild / Test / publishArtifact    := false

ThisBuild / scalaVersion       := Scala213
ThisBuild / crossScalaVersions := List(Scala213, Scala3RC1)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  Scala213  -> "0.1.0",
  Scala3RC1 -> "0.1.0",
)

//=============================================================================
//================================ Dependencies ===============================
//=============================================================================
ThisBuild / resolvers += Resolver.sonatypeRepo("releases")
ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

val pureharmCoreV = "0.1.0-7bc6204" //https://github.com/busymachines/pureharm-core/releases
val pureharmEffectsV = "0.1.0-4946221" //https://github.com/busymachines/pureharm-effects-cats/releases
val scalatestV = "3.2.6" //https://github.com/scalatest/scalatest/releases
val log4catsV = "1.2.0" //https://github.com/typelevel/log4cats/releases
//=============================================================================
//============================== Project details ==============================
//=============================================================================

lazy val root = project
  .in(file("."))
  .aggregate(
    testkitJVM,
    // testkitJS,
  )
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(SonatypeCiReleasePlugin)
  .settings(commonSettings)

lazy val testkit = crossProject(JVMPlatform)
  .settings(commonSettings)
  .settings(dottyJsSettings(ThisBuild / crossScalaVersions))
  .settings(
    name := "pureharm-testkit",
    libraryDependencies ++= Seq(  
      "com.busymachines" %%% "pureharm-core-anomaly" % pureharmCoreV withSources(),
      "com.busymachines" %%% "pureharm-core-sprout" % pureharmCoreV withSources(),
      "com.busymachines" %%% "pureharm-effects-cats" % pureharmEffectsV withSources(),
      "org.typelevel" %%% "log4cats-core" % log4catsV withSources(),
      
     "org.scalatest" %%% "scalatest" % scalatestV  withSources(),
    ),
  )

lazy val testkitJVM = testkit.jvm.settings(
  javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
)

// lazy val testkitJS = testkit.js

//=============================================================================
//================================= Settings ==================================
//=============================================================================

lazy val commonSettings = Seq(
  //required for munit: https://scalameta.org/munit/docs/getting-started.html
  testFrameworks += new TestFramework("munit.Framework"),

  Compile / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "main").toList.map(f => file(f.getPath + major))
    )
  },
  Test / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "test").toList.map(f => file(f.getPath + major))
    )
  },
)
