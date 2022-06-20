package carpetscheme

import com.raquo.laminar.api.L._
import org.scalajs.dom
import d3v4._
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ParentNode
import com.raquo.airstream.ownership.ManualOwner
import urldsl.language.QueryParameters.simpleParamErrorImpl._
import carpetscheme.Events._
import scala.collection.immutable

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

  def allAthletes(names: List[String], inputVars: List[List[Var[Long]]]) = article(
    details(
      cls("start-open"),
      summary(names(0)),
      createTable(inputVars(0))
    ),
    details(
      summary(names(1)),
      createTable(inputVars(1))
    ),
    hr()
  )

  def main(args: Array[String]): Unit =
    documentEvents.onDomContentLoaded.foreach { _ =>
      val defaultNames = List("Athlete A", "Athlete B")
      val athleteNameParams = (listParam[String]("athlete"))
        .matchRawUrl(dom.document.URL)
        .map(list =>
          list.distinct match {
            case a :: b :: tail => List(a, b)
            case a :: tail      => List(a, "Athlete B")
            case immutable.Nil  => defaultNames
          }
        )
        .getOrElse(defaultNames)

      val inputVarsA: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))
      val inputVarsB: List[Var[Long]] = List(Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L), Var(0L))

      val athleteAComb: Signal[BarChart.AthleteResult] =
        Signal.combineSeq(inputVarsA.map(_.signal)).map(r => BarChart.AthleteResult(athleteNameParams(0), r))
      val athleteBComb: Signal[BarChart.AthleteResult] =
        Signal.combineSeq(inputVarsB.map(_.signal)).map(r => BarChart.AthleteResult(athleteNameParams(1), r))

      val appContainer = dom.document.querySelector("#main")
      render(appContainer, allAthletes(athleteNameParams, List(inputVarsA, inputVarsB)))
      dom.document.querySelector(".start-open").setAttribute("open", "")

      implicit val owner = new ManualOwner

      val xScale = BarChart.xScale(athleteNameParams)
      val svg    = BarChart.buildChart(xScale)
      Signal
        .combineSeq(List(athleteAComb, athleteBComb))
        .foreach(data => BarChart.update(svg, xScale, data))

    }(unsafeWindowOwner)

}
