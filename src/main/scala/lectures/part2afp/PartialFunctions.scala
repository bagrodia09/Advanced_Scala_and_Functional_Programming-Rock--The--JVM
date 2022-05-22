package lectures.part2afp

object PartialFunctions extends App {

  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] or Int => Int

  val aFussyFunction = (x: Int) =>
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException

  class FunctionNotApplicableException extends RuntimeException

  val aNicerFussyFunction = (x: Int) =>
    x match {
      case 1 => 42
      case 2 => 56
      case 5 => 999
    }
  // {1, 2, 5} => Int

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  } // partial function value

  println(aPartialFunction(2))

  // Partial Function utilities
  println(aPartialFunction.isDefinedAt(67))

  // lift
  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  val pfChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }

  println(pfChain(2))
  println(pfChain(45))

  // Partial Functions extend normal functions
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }

  // HOFs accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 42
    case 2 => 78
    case 3 => 1000
  }
  println(aMappedList)

  // Partial Functions can only have One parameter type

  val firstFunction: PartialFunction[Int, Int] =
    new PartialFunction[Int, Int] {
      override def apply(v1: Int): Int = v1 match {
        case 1 => 42
        case 2 => 65
        case 3 => 1000
      }

      override def isDefinedAt(x: Int): Boolean =
        x == 1 || x == 2 || x == 3
    }

  val chatbot: PartialFunction[String, String] = {
    case "hello"    => "My name is HAL9000"
    case "goodbye"  => "See you later"
    case "call mom" => "No"
  }

  scala.io.Source.stdin
    .getLines()
    .foreach(line => println("chatbot says: " + chatbot(line)))
}
