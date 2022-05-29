package carpetscheme

import scala.scalajs.js
import d3v4._
import js.JSConverters._
import org.scalajs.dom.EventTarget

object BarChart {

  val marginTop    = 20
  val marginBottom = 20
  val marginRight  = 30
  val marginLeft   = 60
  val width        = 460 - marginLeft - marginRight
  val height       = 400 - marginTop - marginBottom

  val xScale = d3
    .scaleBand()
    .domain(js.Array("Athlete A", "Athlete B"))
    .range(js.Array(0, width))
    .padding(0.2)

  val yScale = d3
    .scaleLinear()
    .domain(js.Array(0, 8000))
    .range(js.Array(height, 0))

  val blocks                         = Events.allEvents.map(_.name).toJSArray
  val colours                        = List("#864b17", "#fc8810", "#edd41a", "#f9f171", "#98c25f", "#5d913b", "#fc6515")
  val colourMap: Map[String, String] = (blocks zip colours).toMap

  def buildChart(): d3selection.Selection[EventTarget] = {

    val svg = d3
      .select("#main")
      .append("div")
      .classed("svg-container", true)
      .append("svg")
      .attr("preserveAspectRatio", "xMinYMin meet")
      .attr("viewBox", "0 0 460 400")
      .classed("svg-content-responsive", true)
      .append("g")
      .attr("transform", "translate(" + marginLeft + "," + marginTop + ")")

    svg
      .append("g")
      .call(d3.axisLeft(yScale))

    svg
      .append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(xScale))

    return svg.append("g") // !
  }

  case class StackedDatum(
      eventName: String,
      data: js.Array[InnerDatum]
  )
  case class InnerDatum(
      begin: Double,
      end: Double,
      athlete: String
  )

  case class AthleteResult(
      name: String,
      results: List[Long]
  )

  def convertToInnerDatums(res: AthleteResult): js.Array[InnerDatum] = {
    val cummalativeResults: List[Long] = res.results.scanLeft(0L)(_ + _).dropRight(0)

    (cummalativeResults zip res.results).map { case (l, r) =>
      InnerDatum(l, l + r, res.name)
    }.toJSArray
  }

  def createStackedData(in: List[AthleteResult]): js.Array[StackedDatum] =
    in.map(convertToInnerDatums)
      .flatMap(blocks zip _)
      .groupMap(_._1)(_._2)
      .toList
      .map { case (event, data) => StackedDatum(event, data.toJSArray) }
      .toJSArray

  def update(svg: d3selection.Selection[EventTarget], results: List[Long]): Unit = {

    val stackedData = createStackedData(List(AthleteResult("Athlete A", results)))

    val u = svg
      .selectAll("g")
      .data(stackedData)

    val p = u
      .enter()
      .append("g")
      .merge(u)
      .attr("fill", (x: StackedDatum) => colourMap.getOrElse(x.eventName, "darkred"))
      .selectAll("rect")
      .data((x: StackedDatum) => x.data)

    p.enter()
      .append("rect")
      .merge(p)
      .attr("x", (x: InnerDatum) => xScale(x.athlete))
      .attr("y", (x: InnerDatum) => yScale(x.end))
      .attr("width", xScale.bandwidth())
      .attr("height", (x: InnerDatum) => yScale(x.begin) - yScale(x.end))
  }

}
