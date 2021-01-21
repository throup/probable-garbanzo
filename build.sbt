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

    libraryDependencies ++= Seq(
      "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
      "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion
    ),

    Compile / PB.targets := Seq(
      scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
    )
  )

lazy val service = (project in file("prime-number-server"))
  .settings(
    shared,
    name := "prime-number-server",
    description := "Service for generating sequences of prime numbers",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test
  )
  .dependsOn(protobuf)

lazy val proxy = (project in file("proxy-service"))
  .settings(
    shared,
    name := "proxy-service",
    description := "Gateway proxy providing REST access to the primes service",
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2",
    libraryDependencies += "org.scalamock" %% "scalamock" % "5.1.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  )
  .dependsOn(protobuf)
