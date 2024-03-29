enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.carpetscheme"
ThisBuild / organizationName := "carpetscheme"

lazy val root = (project in file("."))
  .settings(
    name := "heptathlon",
    resolvers += "jitpack" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      "com.raquo"                        %%% "laminar"       % "0.14.2",
      "com.raquo"                        %%% "domtypes"      % "0.15.1",
      "be.doeraene"                      %%% "url-dsl"       % "0.4.0",
      "com.github.fdietze.scala-js-d3v4" %%% "scala-js-d3v4" % "38364d6",
      "org.scalameta"                    %%% "munit"         % "0.7.29" % Test
    )
  )

Compile / npmDependencies ++= Seq(
  "d3" -> "5.9.2" // https://github.com/d3/d3/releases
)

scalaJSUseMainModuleInitializer := true
