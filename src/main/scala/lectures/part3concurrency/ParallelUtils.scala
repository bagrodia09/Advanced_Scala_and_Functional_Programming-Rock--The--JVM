package lectures.part3concurrency

import scala.collection.parallel.immutable.ParVector
import scala.collection.parallel.ForkJoinTaskSupport
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

object ParallelUtils extends App {

  // parallel collections
  val parVector = ParVector[Int](1, 2, 3)

  def measure[T](operation: => T): Long = {
    val time = System.currentTimeMillis()
    operation
    System.currentTimeMillis() - time
  }

  val list = (1 to 10000000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }
  val parallelTime = measure {
    // list.par.map(_ + 1)
  }

  // configuring
  parVector.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread-safe read
  atomic.set(4) // thread-safe write

  atomic.getAndSet(5) // thread-safe combo

  atomic.compareAndSet(38, 56)

  atomic.updateAndGet(_ + 1) // thread-safe function run
  atomic.getAndUpdate(_ + 1)

  atomic.accumulateAndGet(12, _ + _) // thread-safe accumulation
  atomic.getAndAccumulate(12, _ + _)

}
