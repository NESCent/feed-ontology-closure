import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._

organization  := "org.nescent"

name          := "feed-ontology-closure"

version       := "0.1"

packageArchetype.java_application

scalaVersion  := "2.10.4"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

//resolvers += "Phenoscape Maven repository" at "http://phenoscape.svn.sourceforge.net/svnroot/phenoscape/trunk/maven/repository"

libraryDependencies ++= {
  Seq(
    "net.sourceforge.owlapi" %   "owlapi-distribution" % "3.5.0",
    "org.semanticweb.elk"    %   "elk-owlapi"          % "0.4.1",
    "commons-io"             %   "commons-io"          % "2.4"
  )
}
