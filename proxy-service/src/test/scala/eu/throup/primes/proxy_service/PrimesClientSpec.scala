package eu.throup.primes.proxy_service

import eu.throup.primes.{NextPrimeAfterRequest, NextPrimeAfterResponse}
import eu.throup.primes.NextPrimeAfterServiceGrpc.NextPrimeAfterService

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers._

import scala.concurrent.Future

class PrimesClientSpec extends AsyncFreeSpec with AsyncMockFactory {
  "PrimesClient" - {
    "nextPrimeAfter" - {
      "calls PrimesClient with target value" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 9, 11)

        client.nextPrimeAfter(9)
          .map(_ => succeed)
      }

      "returns response value from PrimesClient" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 9, 11)

        client.nextPrimeAfter(9)
          .map(_ should be(11))
      }
    }

    "allPrimesTo" - {
      "returns an iterator" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 0, 2)
        mockResponse(grpc, 2, 3)

        val iterator = client.allPrimesTo(9)

        iterator.hasNext // Allow steady state

        iterator shouldBe an [Iterator[Int]]
      }

      "makes initial call to server and then waits" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 0, 2)
        mockResponse(grpc, 2, 3)
        mockResponse(grpc, 3, 5).never()
        mockResponse(grpc, 5, 7).never()
        mockResponse(grpc, 7, 11).never()

        val iterator = client.allPrimesTo(9)

        iterator.hasNext // Allow steady state

        iterator shouldBe an [Iterator[Int]]
      }

      "makes enough calls to server to support the iteration" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 0, 2)
        mockResponse(grpc, 2, 3)
        mockResponse(grpc, 3, 5)
        mockResponse(grpc, 5, 7).never()
        mockResponse(grpc, 7, 11).never()

        val iterator = client.allPrimesTo(9)

        iterator.hasNext should be (true)
        iterator.next should be (2)
        iterator.hasNext should be (true)
        iterator.next should be (3)
        iterator.hasNext should be (true)
      }

      "terminates once the limit has been reached" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 0, 2)
        mockResponse(grpc, 2, 3)
        mockResponse(grpc, 3, 5)
        mockResponse(grpc, 5, 7)
        mockResponse(grpc, 7, 11) // This is called as 7 is less than the limit
        mockResponse(grpc, 11, 13).never()

        val iterator = client.allPrimesTo(9)

        iterator.toList shouldBe (List(2, 3, 5, 7))
      }

      "iterator rethrows exception from server client" in {
        val (grpc, client) = freshFixture

        mockResponse(grpc, 0, 2)
        mockResponse(grpc, 2, 3)
        mockResponseFailure(grpc, 3,"Gonna stop here")
        mockResponse(grpc, 5, 7).never()
        mockResponse(grpc, 7, 11).never()

        val iterator = client.allPrimesTo(9)

        the [Exception] thrownBy(iterator.toList) should have message "Gonna stop here"
      }
    }
  }

  private def mockResponse(grpc: NextPrimeAfterService, from: Int, next: Int) =
    (grpc.nextPrimeAfter _)
      .expects(request(from))
      .returning(response(next))

  private def mockResponseFailure(grpc: NextPrimeAfterService, from: Int, message: String) =
    (grpc.nextPrimeAfter _)
      .expects(request(from))
      .returning(Future.failed(new Exception(message)))

  private def request(from: Int) = NextPrimeAfterRequest(from)

  private def response(next: Int) = Future.successful(NextPrimeAfterResponse(next))

  private def freshFixture = {
    val grpc = mock[NextPrimeAfterService]
    val client = PrimesClient(grpc)
    (grpc, client)
  }
}
