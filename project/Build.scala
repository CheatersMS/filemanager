import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "filemanager"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.amazonaws" % "aws-java-sdk" % "1.0.002",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
    organization   := "com.github.theodoreLee"  
  )

}
