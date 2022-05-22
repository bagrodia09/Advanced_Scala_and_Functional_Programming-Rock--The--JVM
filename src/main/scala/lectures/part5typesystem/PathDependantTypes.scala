package lectures.part5typesystem

object PathDependantTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner): Unit = println(i)
    def printGeneral(i: Outer#Inner): Unit = println(i)
  }

  def aMethod: Int = {
    class HelperClass
    type HelperType = String
    2
  }

  val o = new Outer
  val inner = new o.Inner // o.Inner is a type

  val oo = new Outer
  // val otherInner: oo.Inner = new o.Inner // two different types

  o.print(inner)
  // oo.print(inner) // type mismatch

  // ^ path dependant types

  // Outer#Inner
  o.printGeneral(inner)
  oo.printGeneral(inner)
}
