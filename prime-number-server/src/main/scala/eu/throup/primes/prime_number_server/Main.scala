package eu.throup
package primes
package prime_number_server

import scala.concurrent.ExecutionContext

object Main extends App {
  val Port: Int = sys.env.get("PRIME_SERVICE_PORT").flatMap(_.toIntOption).getOrElse(9999)

  GrpcServer(
    NextPrimeAfterServiceGrpc
      .bindService(
        new NextPrimeAfterService, ExecutionContext.global
      ),
    "PrimeServer", Port
  )
}
