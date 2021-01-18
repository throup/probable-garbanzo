val shared = Seq(
  organization := "eu.throup.primes",
  version := "0.1",
  scalaVersion := "2.13.4"
)

lazy val root = (project in file("."))
  .aggregate(protobuf, service, proxy)
  .settings(
    shared,
    name := "primes",
  )
  .dependsOn(service, proxy)

lazy val protobuf = (project in file("protobuf"))
  .settings(
    shared,
    name := "protobuf",
    description := "Protocol Buffer definitions",
  )

lazy val service = (project in file("prime-number-server"))
  .settings(
    shared,
    name := "prime-number-server",
    description := "Service for generating sequences of prime numbers",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
  )
  .dependsOn(protobuf)

lazy val proxy = (project in file("proxy-service"))
  .settings(
    shared,
    name := "proxy-service",
    description := "Gateway proxy providing REST access to the primes service",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
  )
  .dependsOn(protobuf)
