import com.jsuereth.sbtpgp.SbtPgp.autoImport.PgpKeys
import sbt.Keys._
import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import xerial.sbt.Sonatype.SonatypeKeys

object MySettings {

  lazy val scala213 = "2.13.5"
  lazy val scala212 = "2.12.12"
  lazy val scala211 = "2.11.12"

  lazy val commonSettings = Seq(
    crossScalaVersions := Seq(scala213, scala212, scala211),
    scalaVersion := scala213,
    organization := "net.exoego",
    libraryDependencies ++= Def.setting(Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0"
    )).value,
    scalacOptions ++= {
      val scalajsOptions = if (scalaJSVersion.startsWith("0.6."))
        Seq("-P:scalajs:sjsDefinedByDefault")
      else
        Seq()
      Seq("-deprecation", "-feature", "-Xfatal-warnings") ++ scalajsOptions
    },
    // Work around https://github.com/scala-js/scala-js/issues/3612
    scalacOptions in(Compile, doc) := {
      val prev = (scalacOptions in(Compile, doc)).value
      if (scalaJSVersion.startsWith("0.6.") && scalaVersion.value.startsWith("2.13."))
        prev.filter(_ != "-Xfatal-warnings")
      else
        prev
    },
    scalacOptions in Compile ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 12 => Seq(
          "-Ywarn-unused:imports",
        )
        case _ => Nil
      }
    },
  )

  lazy val nonPublishingSetting = Seq(
    skip in publish := true,
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )

  lazy val publishingSettings = Seq(
    licenses += ("BSD 3-Clause", url("http://opensource.org/licenses/BSD-3-Clause")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/exoego/scala-js-jquery"),
      "scm:git:git@github.com:exoego/scala-js-jquery.git",
      Some("scm:git:git@github.com:exoego/scala-js-jquery.git")
    )),
    homepage := scmInfo.value.map(_.browseUrl),
    developers := List(
      Developer(
        id = "exoego",
        name = "TATSUNO Yasuhiro",
        email = "ytatsuno.jp@gmail.com",
        url = url("https://www.exoego.net")
      )
    ),
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageDoc) := true,
    publishArtifact in (Compile, packageSrc) := true,
    publishArtifact in packageDoc := false,
    pomIncludeRepository := { _ =>
      false
    },
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
    publishTo in ThisBuild := SonatypeKeys.sonatypePublishToBundle.value,
    publishMavenStyle := true,
    publishArtifact in packageDoc := false,
    sources in (Compile, doc) := Seq.empty,
  )

}
