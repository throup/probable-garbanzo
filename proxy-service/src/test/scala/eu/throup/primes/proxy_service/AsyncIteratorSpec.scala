package eu.throup.primes.proxy_service

import io.grpc.stub.StreamObserver
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

class AsyncIteratorSpec extends AnyFreeSpec {
  "AsyncIterator" - {
    "implements StreamObserver" in {
      new AsyncIterator[Int]() shouldBe a [StreamObserver[_]]
    }

    "implements Iterator" in {
      new AsyncIterator[Int]() shouldBe a [Iterator[_]]
    }

    "scenarios" - {
      "when one value queued, it is available to iterate" in {
        var iterator = new AsyncIterator[Int]()
        iterator.onNext(123)
        iterator.hasNext should be(true)
        iterator.next should be(123)
      }

      "action in the same thread" in {
        var iterator = new AsyncIterator[Int]()
        iterator.onNext(1)
        iterator.hasNext should be(true)
        iterator.next should be(1)
        iterator.onNext(2)
        iterator.hasNext should be(true)
        iterator.next should be(2)
        iterator.onNext(3)
        iterator.hasNext should be(true)
        iterator.next should be(3)
        iterator.onCompleted()
        iterator.hasNext should be(false)
      }

      "action in separate threads" in {
        var iterator = new AsyncIterator[Int]()

        val thread = new Thread {
          override def run {
            iterator.onNext(4)
            iterator.onNext(5)
            iterator.onNext(6)
            iterator.onCompleted()
          }
        }
        thread.start

        iterator.hasNext should be(true)
        iterator.next should be(4)
        iterator.hasNext should be(true)
        iterator.next should be(5)
        iterator.hasNext should be(true)
        iterator.next should be(6)
        iterator.hasNext should be(false)

        thread.join(2000)
        succeed
      }
    }

    "exception scenarios" - {
      "throws NoSuchElementException if next() called after onCompleted()" in {
        var iterator = new AsyncIterator[Int]()
        iterator.onNext(1)
        iterator.hasNext should be(true)
        iterator.next should be(1)
        iterator.onNext(2)
        iterator.hasNext should be(true)
        iterator.next should be(2)
        iterator.onCompleted()
        iterator.hasNext should be(false)

        an [NoSuchElementException] should be thrownBy iterator.next()
      }

      "rethrows exception passed to onError()" in {
        var iterator = new AsyncIterator[Int]()

        val thread = new Thread {
          override def run {
            iterator.onNext(1)
            iterator.onError(new Exception("I was just thrown"))
          }
        }
        thread.run

        the [Exception] thrownBy iterator.hasNext should have message "I was just thrown"
        the [Exception] thrownBy iterator.next should have message "I was just thrown"

        thread.join(2000)
        succeed
      }
    }
  }
}
