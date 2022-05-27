package carpetscheme

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.util.Try
import scala.util.Failure
import scala.util.Success

object App {

  def pointsHelper(input: String, event: Events.Event): Long =
    Try(input.toDouble) match {
      case Failure(_)     => 0L
      case Success(value) => Events.calculatePoints(value, event)
    }

  def row(event: Events.Event, inputVar: Var[Long]) =
    tr(
      td(event.name),
      td(
        input(
          typ("number"),
          minAttr("0"),
          stepAttr("0.01"),
          width("6rem"),
          onInput.mapToValue.map(s => pointsHelper(s, event)) --> inputVar
        ),
        span(
          paddingLeft("0.5rem"),
          Events.unit(event.`type`)
        )
      ),
      td(
        textAlign("right"),
        child.text <-- inputVar.signal.map(_.toString)
      )
    )

  val inputVars: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))

  val appDiv =
    table(
      thead(
        tr(
          th("Event"),
          th("Result"),
          th("Points")
        )
      ),
      tbody(
        row(Events.HundredHurdles, inputVars(0)),
        row(Events.HighJump, inputVars(1)),
        row(Events.ShotPut, inputVars(2)),
        row(Events.TwoHundred, inputVars(3)),
        row(Events.LongJump, inputVars(4)),
        row(Events.Javelin, inputVars(5)),
        row(Events.EightHundred, inputVars(6)) // TODO: minutes and seconds
      ),
      tfoot(
        tr(
          td(
            colSpan(3),
            textAlign("right"),
            "Total: ",
            child.text <-- Signal.combineSeq(inputVars.map(_.signal)).map(_.sum.toString)
          )
        )
      )
    )

  def main(args: Array[String]): Unit =
    renderOnDomContentLoaded(dom.document.querySelector("#article"), appDiv)
}
