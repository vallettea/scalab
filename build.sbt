name := "A Project"

version := "0.1"

scalaSource in Compile <<= (baseDirectory)(_ / "src")

scalaSource in Test <<= (baseDirectory)(_ / "test")

resolvers += "Sonatype OSS Snapshots Repository" at "http://oss.sonatype.org/content/groups/public/"

resolvers += "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools/"

resolvers += "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray repo" at "http://repo.spray.io/"

resolvers += "maven repo" at "http://repo1.maven.org/maven2/"

libraryDependencies += "org.openstreetmap.osmosis" % "osmosis-core" % "0.43.1"

libraryDependencies += "org.openstreetmap.osmosis" % "osmosis-xml" % "0.43.1"

libraryDependencies += "org.openstreetmap.osmosis" % "osmosis-pbf" % "0.43.1"

libraryDependencies += "org.openstreetmap.osmosis" % "osmosis-tagfilter" % "0.43.1"

libraryDependencies += "org.openstreetmap.osmosis" % "osmosis-pgsnapshot" % "0.43.1"

libraryDependencies += "org.openstreetmap.osmosis" % "osmosis-areafilter" % "0.43.1"

