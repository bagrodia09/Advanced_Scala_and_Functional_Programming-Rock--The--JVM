package lectures.part3concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunication extends App {

  // Producer-consumer problem

  class SimpleContainer {
    private var value: Int = 0

    def isEmpty: Boolean = value == 0
    def set(newValue: Int): Unit = value = newValue
    def get: Int = {
      val result = value
      value = 0
      result
    }
  }

  def naiveProdCons(): Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("consumer waiting")
      while (container.isEmpty) {
        println("consumer actively waiting")
      }

      println("consumer have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("producer computing")
      Thread.sleep(500)
      val value = 42
      println("producer have produced value " + value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  // naiveProdCons()

  // wait and notify
  def smartProdCons(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("consumer waiting")
      container.synchronized {
        container.wait()
      }

      // container must have some value
      println("consumer have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("producer at work")
      Thread.sleep(2000)
      val value = 42

      container.synchronized {
        println("producer producing " + value)
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  // smartProdCons()

  def prodConsLargeBuffer(): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3

    val consumer = new Thread(() => {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("consumer buffer empty waiting")
            buffer.wait()
          }
          // there must be at least one value in the buffer
          val x = buffer.dequeue()
          println("consume consumed " + x)

          buffer.notify()
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("producer buffer is full, waiting")
            buffer.wait()
          }

          // there must be at least one empty space in the buffer
          println("producer producing " + i)
          buffer.enqueue(i)
          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()
  }

  prodConsLargeBuffer()

  class Consumer(id: Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()

      while (true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("consumer buffer empty waiting")
            buffer.wait()
          }
          // there must be at least one value in the buffer
          val x = buffer.dequeue()
          println("consume consumed " + x)

          buffer.notify()
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int)
      extends Thread {
    override def run(): Unit = {
      val random = new Random()
      var i = 0

      while (true) {
        buffer.synchronized {
          if (buffer.size == capacity) {
            println("producer buffer is full, waiting")
            buffer.wait()
          }

          // there must be at least one empty space in the buffer
          println("producer producing " + i)
          buffer.enqueue(i)
          buffer.notify()

          i += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

}
