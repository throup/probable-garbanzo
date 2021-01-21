package eu.throup
package primes
package proxy_service

import cats.data.Kleisli
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._

case class HttpApp(controller: AllPrimesController) {
  def this() = this(new AllPrimesController())

  val definition: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes.of[IO] {
    case GET -> Root / "prime" / IntVar(target) => Ok(controller(target))
  }.orNotFound
}
