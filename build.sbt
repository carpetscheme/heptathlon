enablePlugins(ScalaJSPlugin)

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.carpetscheme"
ThisBuild / organizationName := "carpetscheme"

lazy val root = (project in file("."))
  .settings(
    name := "heptathlon",
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % "0.13.1",
      // "org.scala-js" %%% "scalajs-dom" % "2.2.0",
      "org.scalameta" %%% "munit" % "0.7.29" % Test
    )
  )

scalaJSUseMainModuleInitializer := true
