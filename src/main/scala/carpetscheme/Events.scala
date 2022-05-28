package carpetscheme

import scala.math.{floor, pow}

object Events {

  sealed trait EventType
  case object Running  extends EventType
  case object Jumping  extends EventType
  case object Throwing extends EventType

  def unit(event: EventType) = event match {
    case Jumping  => "cm"
    case Running  => "s"
    case Throwing => "m"
  }

  case class Event(
      name: String,
      `type`: EventType,
      A: Double,
      B: Double,
      C: Double
  )

  private def runningPoints(result: Double, a: Double, b: Double, c: Double): Long =
    (a * pow(b - result, c)).floor.toLong

  private def throwingJumpingPoints(result: Double, a: Double, b: Double, c: Double): Long =
    (a * pow(result - b, c)).floor.toLong

  def calculatePoints(result: Double, event: Event): Long = event.`type` match {
    case Running => runningPoints(result, event.A, event.B, event.C)
    case _       => throwingJumpingPoints(result, event.A, event.B, event.C)
  }

  val HundredHurdles = Event(
    name = "100m Hurdles",
    `type` = Running,
    A = 9.23076,
    B = 26.7,
    C = 1.835
  )

  val HighJump = Event(
    name = "High Jump",
    `type` = Jumping,
    A = 1.84523,
    B = 75.00,
    C = 1.348
  )

  val ShotPut = Event(
    name = "Shot Put",
    `type` = Throwing,
    A = 56.0211,
    B = 1.5,
    C = 1.05
  )

  val TwoHundred = Event(
    name = "200m",
    `type` = Running,
    A = 4.99087,
    B = 42.5,
    C = 1.81
  )

  val LongJump = Event(
    name = "Long Jump",
    `type` = Jumping,
    A = 0.188807,
    B = 210,
    C = 1.41
  )

  val Javelin = Event(
    name = "Javelin",
    `type` = Throwing,
    A = 15.9803,
    B = 3.8,
    C = 1.04
  )

  val EightHundred = Event(
    name = "800m",
    `type` = Running,
    A = 0.11193,
    B = 254,
    C = 1.88
  )

  val allEvents = List(HundredHurdles, HighJump, ShotPut, TwoHundred, LongJump, Javelin, EightHundred)

}
