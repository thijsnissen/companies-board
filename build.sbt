organization := "nl.thijsnissen"
version      := Version.semver
scalaVersion := Version.scala

lazy val root =
  project
    .in(file("."))
    .settings(
      name           := "Companies Board",
      normalizedName := "companies-board",
      description    := "Adapted from ZIO Rite of Passage by Rock the JVM."
    )
    .settings(Settings.common ++ Aliases.common)
    .aggregate(
      foundations,
      application,
      integration,
      libAuth,
      libDb,
      libEmail,
      libHttp
    )

lazy val foundations =
  project
    .in(file("code/foundations"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.foundations)

lazy val application =
  project
    .in(file("code/application"))
    .enablePlugins(JibPlugin)
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.application ++ Dependencies.test)
    .dependsOn(server)

lazy val server: Project =
  project
    .in(file("code/server"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.server ++ Dependencies.test)
    .dependsOn(
      libAuth,
      libDb,
      libEmail,
      libHttp
    )

lazy val integration =
  project
    .in(file("code/integration"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.test)

lazy val libAuth =
  project
    .in(file("code/libs/auth"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.libAuth ++ Dependencies.test)

lazy val libDb =
  project
    .in(file("code/libs/db"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.libDb ++ Dependencies.test)

lazy val libEmail =
  project
    .in(file("code/libs/email"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.libEmail ++ Dependencies.test)

lazy val libHttp =
  project
    .in(file("code/libs/http"))
    .settings(Settings.common)
    .settings(libraryDependencies ++= Dependencies.libHttp)

watchBeforeCommand := Watch.clearScreen

Global / onChangedBuildSource := ReloadOnSourceChanges
