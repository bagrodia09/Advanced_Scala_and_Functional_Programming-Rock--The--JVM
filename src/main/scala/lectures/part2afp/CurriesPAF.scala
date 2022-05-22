package lectures.part2afp

object CurriesPAF extends App {

  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) // Int => Int
  println(add3(5))
  println(superAdder(3)(5)) // curried function

  def curriedAdder(x: Int)(y: Int): Int = x + y // curried method

  val add4: Int => Int = curriedAdder(4)

  // lifting = ETA-EXPANSION

  def inc(x: Int) = x + 1
  List(1, 2, 3).map(inc) // ETA-EXPANSION

  // Partial function application
  val add5 = curriedAdder(5) _ // convert to Int => Int

  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)(y: Int) = x + y

  val add71 = (x: Int) => simpleAddFunction(7, x)
  val add72 = simpleAddFunction.curried(7)
  val add73 = curriedAddMethod(7) _ // PAF
  val add74 = curriedAddMethod(7)(_) // PAF equivalent
  val add75 = simpleAddMethod(7, _: Int) // turning methods into function values
  val add76 = simpleAddFunction(7, _: Int) // same as above

  def concatenate(a: String, b: String, c: String) = a + b + c
  val insertName = concatenate("Hello, I am ", _: String, ", how are you?")
  println(insertName("John"))

  val fillInTheBlanks = concatenate("Hello, ", _: String, _: String)
  println(fillInTheBlanks("John", " asdf"))

  def curriedFormatter(s: String)(number: Double): String = s.format(number)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)

  val simpleFormat = curriedFormatter("%4.2f") _
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(simpleFormat))
  println(numbers.map(curriedFormatter("14.12f")))

  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1
  def method: Int = 42
  def parentMethod(): Int = 42

  byName(42)
  byName(method)
  byName(parentMethod())
  byName(parentMethod) // equal to above
  // byName(() => 42) // wrong
  byName((() => 42)()) // use lambda and call it
  // byName(parentMethod _) // wrong

  // byFunction(45) // wrong
  // byFunction(method) // wrong, compiler doesn't ETA expansion
  byFunction(parentMethod) // compiler does ETA expansion
  byFunction(() => 46)
  byFunction(parentMethod _) // underscore is not needed

}
