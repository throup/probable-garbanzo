package eu.throup
package primes
package proxy_service

import eu.throup.primes.proxy_service.AllPrimesController.{Initiator, Separator, Terminator}

import cats.effect.IO
import fs2.Stream

case class AllPrimesController(client: PrimesClient) {
  def this() = this(PrimesClient("localhost", 9999))

  def apply(target: Int): Stream[IO, String] = {
    Stream(Initiator) ++
      Stream.fromIterator[IO](client.allPrimesTo(target))
        .map(_.toString)
        .flatMap(Stream(_, Separator))
        .dropRight(1) ++
      Stream(Terminator)
  }
}

object AllPrimesController {
  private[proxy_service] val Initiator = ""
  private[proxy_service] val Separator = ","
  private[proxy_service] val Terminator = "."
}
