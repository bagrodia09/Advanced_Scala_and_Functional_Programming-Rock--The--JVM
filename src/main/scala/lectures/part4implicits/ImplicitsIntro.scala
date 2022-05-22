package lectures.part4implicits
import scala.language.implicitConversions

object ImplicitsIntro extends App {
  val pair = "Daniel" -> "555"
  val intPair = 1 -> 2

  case class Person(name: String) {
    def greet = s"My name is $name"
  }

  implicit def fromStringToPerson(str: String): Person = Person(str)

  println("Peter".greet)

  // class A {
  //   def greet: Int = 2
  // }
  // implicit def fromStringToA(str: String): A = new A

  // implicit parameters
  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount: Int = 100

  increment(42)
}
