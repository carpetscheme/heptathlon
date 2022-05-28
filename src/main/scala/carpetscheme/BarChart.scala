package carpetscheme

import scala.scalajs.js
import d3v4._
import js.JSConverters._
import org.scalajs.dom.EventTarget

object BarChart {

  val marginTop    = 10
  val marginRight  = 30
  val marginBottom = 60
  val marginLeft   = 60
  val width        = 460 - marginLeft - marginRight
  val height       = 400 - marginTop - marginBottom

  val blocks = Events.allEvents.map(_.name).toJSArray

  val xScale = d3
    .scaleBand()
    .domain(blocks)
    .range(js.Array(0, width))
    .padding(0.2)

  val yScale = d3
    .scaleLinear()
    .domain(js.Array(0, 1500))
    .range(js.Array(height, 0))

  def buildChart(): d3selection.Selection[EventTarget] = {

    val svg = d3
      .select("#main")
      .append("svg")
      .attr("width", width + marginLeft + marginRight)
      .attr("height", height + marginTop + marginBottom)
      .attr("style", "display: block; margin: 2rem auto;")
      .append("g")
      .attr("transform", "translate(" + marginLeft + "," + marginTop + ")")

    svg
      .append("g")
      .call(d3.axisLeft(yScale))

    svg
      .append("g")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(xScale))
      .selectAll("text")
      .attr("transform", "translate(-10,0)rotate(-45)")
      .style("text-anchor", "end")

    return svg
  }

  case class KeyValue(
      name: String,
      value: Long
  )

  val getName: KeyValue => Double   = (x: KeyValue) => xScale(x.name)
  val getValue: KeyValue => Double  = (x: KeyValue) => yScale(x.value)
  val getHeight: KeyValue => Double = (x: KeyValue) => height.toDouble - yScale(x.value)

  def update(svg: d3selection.Selection[EventTarget], results: List[Long]): Unit = {
    val data: js.Array[KeyValue] =
      (Events.allEvents zip results)
        .map(x => KeyValue(x._1.name, x._2))
        .toJSArray

    val u = svg
      .selectAll("rect")
      .data(data)

    u.enter()
      .append("rect")
      .merge(u)
      .attr("x", getName)
      .attr("y", getValue)
      .attr("width", xScale.bandwidth())
      .attr("height", getHeight)
      .attr("fill", "darkred")
  }

}
