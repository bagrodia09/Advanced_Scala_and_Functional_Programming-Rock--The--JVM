package lectures.part4implicits
import scala.language.implicitConversions
import scala.annotation.tailrec

object PimpMyLibrary extends App {

  implicit class RichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0
    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      @tailrec
      def timesAux(n: Int): Unit =
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }
      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] = {
        if (n <= 0) List()
        else concatenate(n - 1) ++ list
      }
      concatenate(value)
    }
  }

  implicit class RicherInt(richInt: RichInt) {
    def isOdd: Boolean = richInt.value % 2 != 0
  }

  new RichInt(42).sqrt

  42.isEven // = new RichInt(42).isEven
  // Type enrichment = pimping

  import scala.concurrent.duration._
  3.seconds

  // compiler doesn't do multiple implicit searches
  // 42.isOdd // doesn't work

  implicit class RichString(val value: String) extends AnyVal {
    def asInt: Int = Integer.valueOf(value) // java.lang.Integer -> Int
    def encrypt(cypherDistance: Int): String =
      value.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  3.times(() => println("Implicits, lol."))

  // implicit conversions with methods
  implicit def stringToInt(string: String): Int = Integer.valueOf(string)
  println("6" / 2) // please don't use

  class RichAlternativeInt(value: Int)
  implicit def enrich(value: Int): RichAlternativeInt =
    new RichAlternativeInt(value)

  // danger zone

  implicit def intToBoolean(i: Int): Boolean = i == 1

  val aConditionValue = if (3) "OK" else "WRONG"
  println(aConditionValue)

}
