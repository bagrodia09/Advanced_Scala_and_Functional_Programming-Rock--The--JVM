package lectures.part3concurrency

import java.util.concurrent.Executors

object Intro extends App {
  // JVM threads
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Running in parallel")
  })

  aThread.start()
  // gives a signal to the JVM to start a JVM thread
  // Create a JVM thread => OS thread
  aThread.join() // blocks until aThread finishes running

  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
  val threadGoodbye = new Thread(() =>
    (1 to 5).foreach(_ => println("goodbye"))
  )
  // threadHello.start()
  // threadGoodbye.start()

  // Executors
  val pool = Executors.newFixedThreadPool(10)
  // pool.execute(() => println("something in the thread pool"))

  pool.execute(() => {
    Thread.sleep(1000)
    println("done after 1 second")
  })
  pool.execute(() => {
    Thread.sleep(1000)
    println("almost done")
    Thread.sleep(1000)
    println("done after 2 seconds")
  })

  pool.shutdown()
//  pool.execute(() => println("should not appear")) // throws exception in the calling thread

//  pool.shutdownNow() // shutdown all threads and throw exception
  println(pool.isShutdown) // true at this point

  def runInParallel(): Unit = {
    var x = 0
    val thread1 = new Thread(() => {
      x = 1
    })
    val thread2 = new Thread(() => {
      x = 2
    })
    thread1.start()
    thread2.start()
    println(x)
  }
  // for (_ <- 1 to 100) runInParallel
  // race condition

  class BankAccount(@volatile var amount: Int) {
    override def toString: String = "" + amount
  }

  def buy(account: BankAccount, thing: String, price: Int): Unit = {
    account.amount -= price
    println("Bought: " + thing)
    println("Account: " + account)
  }

  for (_ <- 1 to 100) {
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => buy(account, "shoes", 3000))
    val thread2 = new Thread(() => buy(account, "phone", 4000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
    println(" -- ")
  }
  // race condition as well

  // option 1 - use synchronized()
  def buySafe(account: BankAccount, thing: String, price: Int): Unit = {
    account.synchronized {
      // no two threads can evaluate this at the same time
      account.amount -= price
      println("Bought: " + thing)
      println("Account: " + account)
    }
  }

  // option 2 - use @volatile annotation

  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())

}
