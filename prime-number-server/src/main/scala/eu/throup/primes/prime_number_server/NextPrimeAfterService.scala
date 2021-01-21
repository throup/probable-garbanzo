package eu.throup
package primes
package prime_number_server

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class
NextPrimeAfterService(private val function: Int => Int) extends NextPrimeAfterServiceGrpc.NextPrimeAfterService {
  /**
   * The "real" default constructor.
   *
   * If constructed with no explicit function, the service will use
   * [[nextPrimeAfter()]]. This will likely be the desired behaviour for
   * production code, but a mock function can be passed to the primary
   * constructor for testing purposes.
   */
  def this() = this(prime_number_server.nextPrimeAfter)

  override def nextPrimeAfter(request: NextPrimeAfterRequest): Future[NextPrimeAfterResponse] =
    Future {
      NextPrimeAfterResponse(
        function(request.previous)
      )
    }
}
