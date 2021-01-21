package eu.throup
package primes
package proxy_service

import eu.throup.primes.proxy_service.AllPrimesController.{Initiator, Separator, Terminator}

import cats.effect.IO
import fs2.Stream
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.Future

class AllPrimesControllerSpec extends AsyncFreeSpec with AsyncMockFactory {
  "AllPrimesController" - {
    "calls PrimesClient with target value" in {
      val (client, controller) = freshFixture
      (client.allPrimesTo _).expects(9).returning(Seq(2, 3, 5, 7).iterator)

      streamToList(controller(9))
        .map(_ => succeed)
    }

    "returns formatted response based on result from PrimesClient" in {
      val (client, controller) = freshFixture
      (client.allPrimesTo _).expects(*).returning(Seq(2, 3, 5).iterator)

      streamToList(controller(6))
        .map(_ should be (List(Initiator, "2", Separator, "3", Separator, "5", Terminator)))
    }
  }

  private def streamToList[T](output: Stream[IO, T]): Future[List[T]] =
    output
      .compile
      .toList
      .unsafeToFuture

  private def freshFixture = {
    val client = mock[PrimesClient]
    val controller = AllPrimesController(client)
    (client, controller)
  }
}
