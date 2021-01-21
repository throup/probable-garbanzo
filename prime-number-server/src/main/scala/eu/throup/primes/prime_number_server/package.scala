package eu.throup
package primes

import com.typesafe.scalalogging.Logger

package object prime_number_server {
  private val Log = Logger(this.getClass)

  def isPrime(i: Int): Boolean = {
    Log.info("isPrime({})", i)
    if (i < 2)
      false
    else
      (2 until upperBound(i))
        .find(i % _ == 0)
        .forall(_ => false)
  }

  // Optimisation: no need to test past the square root
  // +1 ensures the bound is sufficiently high for square numbers
  private def upperBound(i: Int): Int =
    math.sqrt(i + 1)
      .ceil
      .intValue

  def nextPrimeAfter(i: Int): Int = {
    Log.info("nextPrimeAfter({})", i)

    // We cannot evaluate numbers beyond this value
    if (i == Int.MaxValue)
      throw new IllegalArgumentException

    // Save a little time by beginning no lower than the smallest prime (2)
    LazyList.from(math.max(2, i + 1))
      .find(isPrime)
      .get
  }
}
