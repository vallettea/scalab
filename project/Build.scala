import sbt._
import Keys._

object ScalabBuild extends Build {

        lazy val theSettings = Seq(
          name := "scalab",
          version := "0.0.1",
          organizationName := "snips",
          organization := "net.snips",
          scalaVersion := "2.10.1",
          scalacOptions ++= Seq(
            "-deprecation"
           ),

          libraryDependencies := Seq(
          "com.typesafe.slick" %% "slick" % "1.0.1",
          "org.slf4j" % "slf4j-nop" % "1.6.4",
          "com.github.tminglei" % "slick-pg_2.10.1" % "0.1.1",
          "commons-dbcp" % "commons-dbcp" % "1.4",
          "org.scala-saddle" %% "saddle-core" % "1.3.+" excludeAll(ExclusionRule(organization = "ch.qos.logback"))
        ),
        resolvers += Resolver.mavenLocal,
        resolvers += "sonatype" at "https://oss.sonatype.org/content/groups/public/",
        resolvers += "Adam Gent Maven Repository" at "http://mvn-adamgent.googlecode.com/svn/maven/release",
        resolvers += "maven repo" at "http://repo1.maven.org/maven2/",

        publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository"))),
        publishMavenStyle := true
      )


  lazy val root = Project(id = "pogistan", base = file("."),
    settings = Project.defaultSettings ++ theSettings ++ Seq(
      publishArtifact := false,
      javaOptions += "-Xmx5G"
    ))
}
