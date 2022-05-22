package lectures.part2afp

object Monads extends App {

  // Custom Try monad
  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }
  object Attempt {
    def apply[A](a: => A): Attempt[A] =
      try {
        Success(a)
      } catch {
        case e: Throwable => Failure(e)
      }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B] =
      try {
        f(value)
      } catch {
        case e: Throwable => Failure(e)
      }
  }
  case class Failure(e: Throwable) extends Attempt[Nothing] {
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }

  // val func = (x: Int) => Attempt(x * 2)
  // Success[Int](1).flatMap(func)

  val attempt = Attempt {
    throw new RuntimeException("The Monad")
  }
  println(attempt)

  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value
    def use: A = value
    def flatMap[B](f: A => Lazy[B]): Lazy[B] = f(internalValue)
  }
  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

  val lazyInstance = Lazy {
    println("Something before passing param")
    42
  }

  println(lazyInstance.use)

}
