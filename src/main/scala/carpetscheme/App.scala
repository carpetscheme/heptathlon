package carpetscheme

import com.raquo.laminar.api.L._
import org.scalajs.dom
import scala.util.Try
import scala.util.Failure
import scala.util.Success
import d3v4._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ParentNode
import com.raquo.airstream.ownership.ManualOwner

object App {

  def pointsHelper(input: String, event: Events.Event): Long =
    Try(input.toDouble) match {
      case Failure(_)     => 0L
      case Success(value) => event.points(value)
    }

  def row(event: Events.Event, inputVar: Var[Long]) =
    tr(
      td(event.name),
      td(
        input(
          typ("number"),
          minAttr("0"),
          maxAttr(event.max.map(_.toString()).getOrElse("")),
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

  def createTable(inputVars: List[Var[Long]]) =
    table(
      thead(
        tr(
          th("Event"),
          th("Result"),
          th("Points")
        )
      ),
      tbody(
        (Events.allEvents zip inputVars).map(p => row(p._1, p._2))
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

  val inputVarsOne: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))
  val inputVarsTwo: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))

  val comb: Signal[List[Long]] = Signal.combineSeq(inputVarsOne.map(_.signal)).map(_.toList)

  val allAthletes = article(
    details(
      cls("start-open"),
      summary("Athlete A"),
      createTable(inputVarsOne)
    ),
    details(
      summary("Athlete B"),
      createTable(inputVarsTwo)
    ),
    hr()
  )

  def main(args: Array[String]): Unit =
    documentEvents.onDomContentLoaded.foreach { _ =>
      val appContainer = dom.document.querySelector("#main")
      render(appContainer, allAthletes)
      dom.document.querySelector(".start-open").setAttribute("open", "")
      val svg            = BarChart.buildChart()
      implicit val owner = new ManualOwner
      comb.foreach(data => BarChart.update(svg, data))
    }(unsafeWindowOwner)

}
