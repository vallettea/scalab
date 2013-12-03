import sbt._
import Keys._
import com.github.retronym.SbtOneJar


object ApplicationBuild extends Build {

    def standardSettings = Seq(exportJars := true) ++ Defaults.defaultSettings

    lazy val main = Project(
            id = "sampler", 
            base = file(".")
    ).settings((standardSettings ++ SbtOneJar.oneJarSettings):_*)

}