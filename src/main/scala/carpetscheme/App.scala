package carpetscheme

import com.raquo.laminar.api.L._
import org.scalajs.dom
import d3v4._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ParentNode
import com.raquo.airstream.ownership.ManualOwner
import carpetscheme.Events._

object App {

  def buildInput(event: Events.Event, inputVar: Var[Long]): ReactiveHtmlElement[org.scalajs.dom.html.Input] =
    event.`type` match {
      case MiddleDistance =>
        input(
          minAttr("0"),
          maxAttr(event.max.map(_.toString()).getOrElse("")),
          width("6rem"),
          typ("text"),
          inputMode("numeric"),
          onInput.mapToValue
            .map(_.filter(c => Character.isDigit(c) || c == ':' || c == '.'))
            .setAsValue
            .map(s => EightHundredPointsHelper(s)) --> inputVar
        )
      case _ =>
        input(
          minAttr("0"),
          maxAttr(event.max.map(_.toString()).getOrElse("")),
          stepAttr("0.01"),
          width("6rem"),
          typ("number"),
          inputMode("decimal"),
          onInput.mapToValue.map(s => pointsHelper(s, event)) --> inputVar
        )
    }

  def row(event: Events.Event, inputVar: Var[Long]) =
    tr(
      td(event.name),
      td(
        buildInput(event, inputVar),
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

  val nameA                       = "Athlete A"
  val nameB                       = "Athlete B"
  val inputVarsA: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))
  val inputVarsB: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))

  val athleteAComb: Signal[BarChart.AthleteResult] =
    Signal.combineSeq(inputVarsA.map(_.signal)).map(r => BarChart.AthleteResult(nameA, r))
  val athleteBComb: Signal[BarChart.AthleteResult] =
    Signal.combineSeq(inputVarsB.map(_.signal)).map(r => BarChart.AthleteResult(nameB, r))

  val tester: Signal[Seq[BarChart.AthleteResult]] = Signal.combineSeq(List(athleteAComb, athleteBComb))

  val allAthletes = article(
    details(
      cls("start-open"),
      summary(nameA),
      createTable(inputVarsA)
    ),
    details(
      summary(nameB),
      createTable(inputVarsB)
    ),
    hr()
  )

  def main(args: Array[String]): Unit =
    documentEvents.onDomContentLoaded.foreach { _ =>
      val appContainer = dom.document.querySelector("#main")
      render(appContainer, allAthletes)
      dom.document.querySelector(".start-open").setAttribute("open", "")

      implicit val owner = new ManualOwner

      val xScale = BarChart.xScale(List(nameA, nameB))
      val svg    = BarChart.buildChart(xScale)
      Signal
        .combineSeq(List(athleteAComb, athleteBComb))
        .foreach(data => BarChart.update(svg, xScale, data))

    }(unsafeWindowOwner)

}
