package eu.throup
package primes
package prime_number_server

import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers._

class NextPrimeAfterServiceSpec extends AsyncFreeSpec {
  "NextPrimeAfterService" - {
    "implements NextPrimeAfterServiceGrpc.NextPrimeAfterService" in {
      new NextPrimeAfterService() shouldBe a [NextPrimeAfterServiceGrpc.NextPrimeAfterService]
    }

    "nextPrimeAfter" - {
      "passes request parameter to the function" in {
        var received = -1
        val function = (i: Int) => {
          received = i
          321
        }
        val service = new NextPrimeAfterService(function)

        service.nextPrimeAfter(NextPrimeAfterRequest(123))
          .map(_ => received should be (123))
      }

      "returns response from the function" in {
        val function = (i: Int) => 321
        val service = new NextPrimeAfterService(function)

        service.nextPrimeAfter(NextPrimeAfterRequest(123))
          .map(_.next should be (321))
      }

      "returns future exception when function throws" in {
        val function = (i: Int) => throw new IllegalArgumentException
        val service = new NextPrimeAfterService(function)

        recoverToSucceededIf[IllegalArgumentException] {
          service.nextPrimeAfter(NextPrimeAfterRequest(123))
        }
      }
    }
  }
}
