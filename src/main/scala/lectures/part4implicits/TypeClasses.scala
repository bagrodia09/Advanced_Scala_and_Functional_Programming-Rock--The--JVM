package lectures.part4implicits

object TypeClasses extends App {

  trait HTMLWritable {
    def toHTML: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    def toHTML: String = s"<div>$name ($age) <a href=$email>Email</a></div>"
  }

  User("John", 23, "john@test.com").toHTML

  /**
    * 1 - works for custom types only
    * 2 - only one implementation
    */
  // option 2 - use pattern matching
  // object HTMLSerializerPM {
  //   def serializeToHTML(value: any) = value match {
  //     case User(n, a, e)  =>
  //     case java.util.Date =>
  //     case _              =>
  //   }
  // }

  /**
    * 1 - no type safety
    * 2 - need to modify code
    * 3 - only one implementation
    */
  // option 3 - type class
  trait HTMLSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HTMLSerializer[User] {
    def serialize(user: User): String =
      s"<div>${user.name} (${user.age}) <a href=${user.email}>Email</a></div>"
  }

  val john = User("John", 23, "john@test.com")
  println(UserSerializer.serialize(john))

  /**
    * 1 - can implement serializers for other types
    * 2 - can define multiple serializers for one type
    */
  trait TypeClassTemplate[T] {
    def action(value: T): String
  }
  object TypeClassTemplate {
    def apply[T](implicit instance: TypeClassTemplate[T]): TypeClassTemplate[T] = instance
  }

  object HTMLSerializer {
    def serialize[T](value: T)(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)

    // def apply[T](implicit serializer: HTMLSerializer[T]): serializer
  }

  implicit object IntSerializer extends HTMLSerializer[Int] {
    def serialize(value: Int): String = s"<div>$value</div>"
  }

  println(HTMLSerializer.serialize(42))
  // println(HTMLSerializer[User].serialize(john))

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }

  object Equal {
    def apply[T](a: T, b: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer(a, b)
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  // Ad-hoc polymorphism
  val anotherJohn = User("John", 25, "john2@test.com")
  println(Equal(john, anotherJohn))

  // part 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HTMLSerializer[T]): String =
      serializer.serialize(value)
  }

  println(
    john.toHTML
  ) // = println(new HTMLEnrichment[User](john).toHTML(UserSerializer))
  /**
    * 1 - extend to new types
    * 2 - choose implementation (import serializer or pass it explicitly)
    * 3 - super expressive!
    */
  println(2.toHTML)

  /** Elements of enhancing with Type Class
    * - type class itself
    * - type class instances (some of which are implicit)
    *  - conversions with implicit classes
    */
  implicit class TypeSafeEqual[T](value: T) {
    def ===(other: T)(implicit equalizer: Equal[T]): Boolean =
      equalizer(value, other)
    def !==(other: T)(implicit equalizer: Equal[T]): Boolean =
      !equalizer(value, other)
  }

  println(john == anotherJohn)
  // Type Safe

  // println(john == 43) // compiler warns
  // println(john === 43) // compiler errors

  // context bounds
  def htmlBoilerplate[T](
      content: T
  )(implicit serializer: HTMLSerializer[T]): String =
    s"<html><body>${content.toHTML(serializer)}</body></html>"

  def htmlSugar[T: HTMLSerializer](content: T): String =
    s"<html><body>${content.toHTML}</body></html>"

  // implicitly
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")

  // in some other part of the code
  val standardPerms = implicitly[Permissions]

}
