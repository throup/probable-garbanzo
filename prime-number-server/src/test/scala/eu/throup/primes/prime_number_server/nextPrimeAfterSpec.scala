package eu.throup
package primes
package prime_number_server

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

class nextPrimeAfterSpec extends AnyFreeSpec {
  "nextPrimeAfter" - {
    "takes a single Int parameter" in {
      nextPrimeAfter(1)
    }

    "returns an Int" in {
      val output: Int = nextPrimeAfter(1)
    }

    "sequence of known results" - {
      runDataSet(Map(
        0 -> 2,
        1 -> 2,
        2 -> 3,
        3 -> 5,
        4 -> 5,
        5 -> 7,
        6 -> 7,
        7 -> 11,
        8 -> 11,
      ))
    }

    "edge cases" - {
      "large numbers" - {
        runTestCase(Int.MaxValue - 1, Int.MaxValue)

        s"input: ${Int.MaxValue} => IllegalArgumentException" in {
          an [IllegalArgumentException] should be thrownBy nextPrimeAfter(Int.MaxValue)
        }
      }

      "negative numbers" - {
        runDataSet(Map(
          -1 -> 2,
          Int.MinValue + 1 -> 2,
          Int.MinValue -> 2
        ))
      }
    }
  }

  private def runDataSet(data: Map[Int, Int]): Unit = {
    for ((input, expected) <- data) {
      runTestCase(input, expected)
    }
  }

  private def runTestCase(input: Int, expected: Int): Unit = {
    s"input: $input => expected output: $expected" in {
      nextPrimeAfter(input) should equal (expected)
    }
  }
}
