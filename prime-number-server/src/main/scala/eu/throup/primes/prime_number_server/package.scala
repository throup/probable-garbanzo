package eu.throup
package primes

package object prime_number_server {
  def isPrime(i: Int): Boolean = {
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
}
