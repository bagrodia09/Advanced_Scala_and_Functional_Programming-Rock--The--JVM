package lectures.part5typesystem
import scala.language.implicitConversions
import scala.language.postfixOps

object StructuralTypes extends App {

  // structural types

  type JavaCloseable = java.io.Closeable

  class HipsterCloseable {
    def close(): Unit = println("Closing")
  }

  // def closeQuietly(Closeable: JavaCloseable OR HipsterCloseable)

  // structural type
  type UnifiedCloseable = {
    def close(): Unit
  }
  def closeQuietly(unifiedCloseable: UnifiedCloseable): Unit =
    unifiedCloseable.close()

  closeQuietly(new JavaCloseable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterCloseable)

  // type refinements
  type AdvancedCloseable = JavaCloseable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaCloseable {
    def close(): Unit = println("Java closes")
    def closeSilently(): Unit = println("Java closes silently")
  }

  def closeShh(advCloseable: AdvancedCloseable): Unit =
    advCloseable.closeSilently()

  closeShh(new AdvancedJavaCloseable)

  // using structural types as standalone types

  def altClose(closeable: { def close(): Unit }): Unit = closeable.close()

  // type-checking -> duck typing

  type SoundMaker = {
    def makeSound(): Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark")
  }

  class Car {
    def makeSound(): Unit = println("vroom")
  }

  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // static duck typing

  // caveat - duck types are based on reflection
}
