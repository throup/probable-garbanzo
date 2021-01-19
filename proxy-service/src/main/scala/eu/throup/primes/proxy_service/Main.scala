package eu.throup
package primes
package proxy_service

import cats.effect._
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.global

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val Host: String = sys.env.getOrElse("PRIME_PROXY_HOST", "0.0.0.0")
    val Port: Int = sys.env.get("PRIME_PROXY_PORT").flatMap(_.toIntOption).getOrElse(8080)

    BlazeServerBuilder[IO](global)
      .bindHttp(Port, Host)
      .withHttpApp((new HttpApp).definition)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}
