package eu.throup
package primes
package proxy_service

import cats.effect._
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp((new HttpApp).definition)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
