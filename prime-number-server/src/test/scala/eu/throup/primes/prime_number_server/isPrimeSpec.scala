package eu.throup
package primes
package prime_number_server

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

class isPrimeSpec extends AnyFreeSpec {
  "isPrime" - {
    "takes a single Int parameter" in {
      isPrime(1)
    }

    "returns a Boolean" in {
      val output: Boolean = isPrime(1)
    }

    "sequence of known results" - {
      runDataSet(Map(
        0 -> false,
        1 -> false,
        2 -> true,
        3 -> true,
        4 -> false,
        5 -> true,
        6 -> false,
        7 -> true,
        8 -> false,
        9 -> false,
        10 -> false,
        11 -> true,
        24 -> false,
        25 -> false,
      ))
    }

    "edge cases" - {
      "large numbers" - {
        runDataSet(Map(
          Int.MaxValue - 1 -> false,
          Int.MaxValue -> true
        ))
      }

      "negative numbers" - {
        runDataSet(Map(
          -1 -> false,
          Int.MinValue + 1 -> false,
          Int.MinValue -> false
        ))
      }
    }
  }

  private def runDataSet(data: Map[Int, Boolean]): Unit = {
    for ((input, expected) <- data) {
      s"input: $input => expected output: $expected" in {
        isPrime(input) should equal (expected)
      }
    }
  }
}
