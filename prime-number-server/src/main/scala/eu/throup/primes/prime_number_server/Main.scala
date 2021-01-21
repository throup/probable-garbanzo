package eu.throup
package primes
package prime_number_server

import scala.concurrent.ExecutionContext

object Main extends App {
  GrpcServer(
    NextPrimeAfterServiceGrpc
      .bindService(
        new NextPrimeAfterService, ExecutionContext.global
      ),
    "PrimeServer", 9999
  )
}


