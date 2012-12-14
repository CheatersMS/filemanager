import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "filemanager"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    //Amazon S3 SDK
    "com.amazonaws" % "aws-java-sdk" % "1.0.002",

    //imagescalr - Java Image Scaling Library
    "org.imgscalr" % "imgscalr-lib" % "4.2",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
    organization := "com.github.theodoreLee",
    publishTo := Some(Resolver.file("Github Pages", Path.userHome / "git" / "theodoreLee.github.com" / "maven" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))
  )

}
