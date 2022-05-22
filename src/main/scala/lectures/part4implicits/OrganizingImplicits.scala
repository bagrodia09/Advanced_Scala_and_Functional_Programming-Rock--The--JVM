package lectures.part4implicits

object OrganizingImplicits extends App {

  // scala.Predef contains typically used implicits, that can be overridden
  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1, 4, 5, 3, 2).sorted)

  /**
    * Implicits (used as implicit parameters):
    *   - val/var
    *   - object
    *   - accessor methods - definitions with no parentheses
    */
  case class Person(name: String, age: Int)
  val persons = List(
    Person("John", 30),
    Person("Amy", 22),
    Person("Bob", 45)
  )

  object Person {
    implicit val personOrdering: Ordering[Person] =
      Ordering.fromLessThan(_.name < _.name)
  }

  println(persons.sorted)

  /**
    * Implicit scope
    *  - normal scope = Local Scope
    *  - imported scope
    *  - companions of all types involved in the method signature
    *   - List
    *   - Ordering
    *   - all the types involved = A or any supertype
    */
  object AlphabeticNameOrdering {
    implicit val personOrdering: Ordering[Person] =
      Ordering.fromLessThan(_.name < _.name)
  }

  object AgeOrdering {
    implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  }

  import AlphabeticNameOrdering._
  println(persons.sorted)

  case class Purchase(nUnits: Int, unitPrice: Double)
  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan((a, b) =>
        a.nUnits * a.unitPrice < b.nUnits * b.unitPrice
      )
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.nUnits < _.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] =
      Ordering.fromLessThan(_.unitPrice < _.unitPrice)
  }
}
