package carpetscheme

import Events._

class EventsTest extends munit.FunSuite {

  test("Caclculate total points") {
    val results: List[Long] = List(
      calculatePoints(13.77, HundredHurdles),
      calculatePoints(188, HighJump),
      calculatePoints(13.15, ShotPut),
      calculatePoints(23.73, TwoHundred),
      calculatePoints(661, LongJump),
      calculatePoints(50.73, Javelin),
      calculatePoints(131.53, EightHundred)
    )

    val expectedPoints = List(
      1011, 1080, 737, 1007, 1043, 874, 942
    ).map(_.toLong)
    assertEquals(results, expectedPoints)

    val score = results.sum.toInt
    assertEquals(score, 6694)
  }

    test("Caclculate total points again") {
    val results: List[Long] = List(
      calculatePoints(13.05, HundredHurdles),
      calculatePoints(167, HighJump),
      calculatePoints(12.87, ShotPut),
      calculatePoints(22.92, TwoHundred),
      calculatePoints(645, LongJump),
      calculatePoints(48.74, Javelin),
      calculatePoints(134.24, EightHundred)
    )

    val expectedPoints = List(
      1117, 818, 719, 1087, 991, 836, 904
    ).map(_.toLong)
    assertEquals(results, expectedPoints)

    val score = results.sum.toInt
    assertEquals(score, 6472)
  }

}
