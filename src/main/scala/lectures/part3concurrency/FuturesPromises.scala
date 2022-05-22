package lectures.part3concurrency

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure
import scala.util.Random
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Promise

object FuturesPromises extends App {

  def calculateMeaningOfLife: Int = {
    Thread.sleep(2000)
    42
  }

  val aFuture = Future {
    calculateMeaningOfLife
  }

  println(aFuture.value) // Option[Try[Int]]

  aFuture.onComplete {
    case Success(value)     => println(value)
    case Failure(exception) => println(exception)
  }

  Thread.sleep(3000)

  case class Profile(id: String, name: String) {
    def poke(anotherProfile: Profile): Unit =
      println(s"${this.name} is poking ${anotherProfile.name}")
  }

  object SocialNetwork {
    val names: Map[String, String] = Map(
      "fb.id.1-luck" -> "Mark",
      "fb.id.2-bill" -> "Bill",
      "fb.id.0-dummy" -> "Dummy"
    )

    val friends: Map[String, String] = Map(
      "fb.id.1-luck" -> "fb.id.2-bill"
    )

    val random = new Random()

    // API
    def fetchProfile(id: String): Future[Profile] = Future {
      // Fetching from network like
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  val mark = SocialNetwork.fetchProfile("fb.id.1-luck")
  mark.onComplete {
    case Success(markProfile) =>
      val bill = SocialNetwork.fetchBestFriend(markProfile)
      bill.onComplete {
        case Success(billProfile) => markProfile.poke(billProfile)
        case Failure(exception)   => exception.printStackTrace()
      }
    case Failure(exception) => exception.printStackTrace()
  }
  Thread.sleep(1000)

  // functional composition of futures
  // map, flatMap, filter
  val nameOnTheWall = mark.map(profile => profile.name)

  val marksBestFriend =
    mark.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  val lucksBestFriendRestricted =
    marksBestFriend.filter(profile => profile.name.startsWith("L"))

  // for-comprehensions
  for {
    mark <- SocialNetwork.fetchProfile("fb.id.1-luck")
    bill <- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill)

  // fallbacks
  val aProfileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case _: Throwable => Profile("fb.id.0-dummy", "Forever Alone")
  }

  val aFetchedProfileNoMatterWhat =
    SocialNetwork.fetchProfile("unknown id").recoverWith {
      case _: Throwable => SocialNetwork.fetchProfile("fb.id.0-dummy")
    }

  val fallbackResult = SocialNetwork
    .fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("fb.id.0-dummy"))

  case class User(name: String)
  case class Transaction(
      sender: String,
      receiver: String,
      amount: Double,
      status: String
  )

  object BankingApp {
    val name = "The Bank App"

    def fetchUser(name: String): Future[User] = Future {
      // some long computation
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(
        user: User,
        merchantName: String,
        amount: Double
    ): Future[Transaction] = Future {
      // another long computation
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "success")
    }

    def purchase(
        username: String,
        item: String,
        merchantName: String,
        cost: Double
    ): String = {
      val transactionStatysFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status

      Await.result(transactionStatysFuture, 2.seconds) // implicit conversions
    }
  }

  println(BankingApp.purchase("John", "TV", "Amazon", 1000))

  // Promises
  val promise = Promise[Int]()
  val future = promise.future

  future.onComplete {
    case Success(value)     => println("consumer have received a value " + value)
    case Failure(exception) => println(exception.printStackTrace())
  }

  val producer = new Thread(() => {
    println("producer crunching numbers...")
    Thread.sleep(500)
    promise.success(42)
    println("producer done")
  })

  producer.start()
  Thread.sleep(1000)

  // #1
  def fulfillImmediately[T](value: T): Future[T] = Future(value)

  // #2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] =
    first.flatMap(_ => second)

  // #3
  def first[A](fa: Future[A], fb: Future[A]): Future[A] = {
    val promise = Promise[A]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }
}
