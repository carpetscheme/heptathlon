package carpetscheme

import scala.math.{floor, pow}
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import org.scalajs.dom.intl.NumberFormat

object Events {

  sealed trait EventType
  case object Sprint         extends EventType
  case object Jumping        extends EventType
  case object Throwing       extends EventType
  case object MiddleDistance extends EventType

  def unit(event: EventType) = event match {
    case Sprint         => "s"
    case Jumping        => "m"
    case Throwing       => "m"
    case MiddleDistance => "m:ss"
  }

  def pointsHelper(in: String, event: Event): Long =
    Try(in.replace(',', '.').toDouble).filter(_ != 0.0).map(event.points).getOrElse(0L)

  def EightHundredPointsHelper(in: String): Long = in.split(":") match {
    case arr if arr.length > 2 => 0L
    case Array(m, s) =>
      val comb = for {
        ms <- Try(m.toDouble).map(_ * 60)
        ss <- Try(s.toDouble)
      } yield (ms + ss)

      comb.map(EightHundred.points).getOrElse(0L)
    case arr => pointsHelper(arr(0), EightHundred)
  }

  private def runningPoints(result: Double, a: Double, b: Double, c: Double): Long =
    (a * pow(b - result, c)).floor.toLong

  private def throwingJumpingPoints(result: Double, a: Double, b: Double, c: Double): Long =
    (a * pow(result - b, c)).floor.toLong

  case class Event(
      name: String,
      `type`: EventType,
      A: Double,
      B: Double,
      C: Double
  ) {
    def points(result: Double): Long = this.`type` match {
      case Jumping  => throwingJumpingPoints(result * 100, this.A, this.B, this.C)
      case Throwing => throwingJumpingPoints(result, this.A, this.B, this.C)
      case _        => runningPoints(result, this.A, this.B, this.C)
    }

    def max: Option[Double] = if (this.`type` == Sprint) Some(this.B) else None

  }

  val HundredHurdles = Event(
    name = "100m Hurdles",
    `type` = Sprint,
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
    `type` = Sprint,
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
    `type` = MiddleDistance,
    A = 0.11193,
    B = 254,
    C = 1.88
  )

  val allEvents = List(HundredHurdles, HighJump, ShotPut, TwoHundred, LongJump, Javelin, EightHundred)

}
