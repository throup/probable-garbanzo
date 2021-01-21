package eu.throup
package primes
package proxy_service

import com.typesafe.scalalogging.Logger
import eu.throup.primes.NextPrimeAfterServiceGrpc.NextPrimeAfterService
import io.grpc.Channel
import io.grpc.netty.NettyChannelBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class PrimesClient(client: NextPrimeAfterService) {
  private val Log = Logger(this.getClass)

  def nextPrimeAfter(from: Int): Future[Int] = {
    Log.debug("nextPrimeAfter({}) - fetching result.", from)
    client.nextPrimeAfter(NextPrimeAfterRequest(from))
      .map(_.next)
  }

  def allPrimesTo(limit: Int): Iterator[Int] = {
    Log.debug("allPrimesTo({}) - preparing iterator.", limit)
    val iterator = new AsyncIterator[Int]
    queueNextPrime(0, limit, iterator)
    Log.debug("allPrimesTo({}) - returning iterator.", limit)
    iterator
  }

  private def queueNextPrime(from: Int, limit: Int, iterator: AsyncIterator[Int]): Unit = {
    Log.debug("queueNextPrime(from: {}, limit: {}, iterator) - initiating request.", from, limit)
    nextPrimeAfter(from)
      .onComplete {
        case Success(p: Int) =>
          Log.debug("queueNextPrime(from: {}, limit: {}, iterator) - received {}.", from, limit, p)
          if (p <= limit) iterator.onNext(p)
          if (p < limit)
            queueNextPrime(p, limit, iterator)
          else
            iterator.onCompleted()

        case Failure(t) => iterator.onError(t)
      }
  }
}

object PrimesClient {
  def apply(host: String, port: Int): PrimesClient =
    PrimesClient(NettyChannelBuilder.forAddress(host, port).usePlaintext.build)

  def apply(channel: Channel): PrimesClient =
    PrimesClient(NextPrimeAfterServiceGrpc.stub(channel))
}
