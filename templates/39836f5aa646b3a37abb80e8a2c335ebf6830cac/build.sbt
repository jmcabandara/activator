name := "hello-akka"

version := "1.0"

scalaVersion := "2.10.1"

scalaSource in Compile <<= baseDirectory / "app"

javaSource in Compile <<= baseDirectory / "app"

sourceDirectory in Compile <<= baseDirectory / "app"

scalaSource in Test <<= baseDirectory / "test"

javaSource in Test <<= baseDirectory / "test"

sourceDirectory in Test <<= baseDirectory / "test"

resourceDirectory in Compile <<= baseDirectory / "conf"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.1.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.1.2",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.10-M3" % "test->default"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-v")
