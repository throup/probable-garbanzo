package eu.throup.primes.proxy_service

import com.typesafe.scalalogging.Logger
import io.grpc.stub.StreamObserver

import java.util.concurrent.Semaphore

/**
 * Iterator which may be returned by a gRPC client as it streams the data from
 * the server. It deliberately extends [[StreamObserver]] to enable it to be
 * used by a streaming command.
 *
 * Adapted from *IteratorWithSemaphorSolution* on StackOverflow
 * [[https://stackoverflow.com/a/38815226/2406861]]
 */
class AsyncIterator[T] extends StreamObserver[T] with Iterator[T] {
  private val Log = Logger(this.getClass)

  var lastEntry: Option[T] = None
  var thrown: Option[Throwable] = None
  var waitingToReceive = new Semaphore(1)
  var waitingToBeConsumed = new Semaphore(1)
  var eof = false

  waitingToReceive.acquire()

  override def onNext(value: T): Unit = {
    Log.debug("onNext({}) - acquiring waitingToBeConsumed semaphore.", value)
    waitingToBeConsumed.acquire()
    Log.debug("onNext({}) - setting next entry.", value)
    lastEntry = Some(value)
    Log.debug("onNext({}) - releasing waitingToReceive semaphore.", value)
    waitingToReceive.release()
  }

  override def onError(t: Throwable): Unit = {
    Log.info("onError() - storing throwable.", t)
    thrown = Some(t)
    Log.debug("onError() - releasing waitingToReceive semaphore.")
    waitingToReceive.release()
  }

  override def onCompleted(): Unit = {
    Log.debug("onCompleted() - flagging eof.")
    eof = true
    Log.debug("onCompleted() - releasing waitingToReceive semaphore.")
    waitingToReceive.release()
  }

  override def hasNext: Boolean = {
    rethrowOnError("hasNext()")

    Log.debug("hasNext() - acquiring waitingToReceive semaphore.")
    waitingToReceive.acquire()
    Log.debug("hasNext() - releasing waitingToReceive semaphore.")
    waitingToReceive.release()
    val hasNext = !eof || lastEntry.nonEmpty
    Log.debug("hasNext() - returning {}.", hasNext)
    hasNext
  }

  override def next(): T = {
    rethrowOnError("next()")

    Log.debug("next() - acquiring waitingToReceive semaphore.")
    waitingToReceive.acquire()
    if (eof && lastEntry.isEmpty) {
      Log.debug("next() - no next element.")
      throw new NoSuchElementException
    }
    val entryToReturn = lastEntry.get
    lastEntry = None
    Log.debug("next() - releasing waitingToBeConsumed semaphore.")
    waitingToBeConsumed.release()
    Log.debug("next() - returning {}.", entryToReturn)
    entryToReturn
  }

  private def rethrowOnError(source: String): Unit = {
    thrown.foreach { throwable =>
      Log.info(s"$source - rethrowing.", throwable)
      throw throwable
    }
  }
}
