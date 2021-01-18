package eu.throup
package primes
package prime_number_server

import org.scalatest.freespec.AnyFreeSpec

class SetupSpec extends AnyFreeSpec {
  "prove the test suite works" - {
    "simple sum" in {
      assert(2 + 3 == 5)
    }

    "run some package function" in {
      assert(myFunction(123) == 456)
    }
  }
}
