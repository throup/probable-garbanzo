package eu.throup.primes.proxy_service

import cats.effect.IO
import fs2.Stream
import org.http4s.{Method, Request, Response, Uri}
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.Future

class HttpAppSpec extends AsyncFreeSpec with AsyncMockFactory {
  "HttpAppSpec" - {
    "prime path should defer to controller" in {
      val (controller, router) = freshFixture
      (controller.apply _).expects(1234).returning(Stream("abc", "def"))

      val request = Request[IO](
        method = Method.GET,
        uri = Uri(path = "prime/1234")
      )

      val response = router(request)

      bodyOf(response)
        .map(_ should be ("abcdef"))
    }

    "prime path with invalid parameter should be not found" in {
      val (controller, router) = freshFixture
      (controller.apply _).expects(*).never()

      val request = Request[IO](
        method = Method.GET,
        uri = Uri(path = "prime/bad_param")
      )

      val response = router(request)

      bodyOf(response)
        .map(_ should be ("Not found"))
    }

    "other random path should be not found" in {
      val (controller, router) = freshFixture
      (controller.apply _).expects(*).never()

      val request = Request[IO](
        method = Method.GET,
        uri = Uri(path = "what/do/you/call/this")
      )

      val response = router(request)

      bodyOf(response)
        .map(_ should be ("Not found"))
    }
  }

  private def bodyOf(response: IO[Response[IO]]): Future[String] =
    response
      .unsafeToFuture()
      .map(_.body)
      .map(_.compile)
      .map(_.toList)
      .flatMap(_.unsafeToFuture)
      .map(l => l.map(_.toChar))
      .map(_.mkString)

  private def freshFixture = {
    val controller = mock[AllPrimesController]
    val app = HttpApp(controller)
    val definition = app.definition
    val router = definition.run
    (controller, router)
  }
}
