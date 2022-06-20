package carpetscheme

import Events._

class EventsTest extends munit.FunSuite {

  test("Calculate total points") {
    val results: List[Long] = List(
      HundredHurdles.points(13.77),
      HighJump.points(1.88),
      ShotPut.points(13.15),
      TwoHundred.points(23.73),
      LongJump.points(6.61),
      Javelin.points(50.73),
      EightHundred.points(131.53)
    )

    val expectedPoints = List(
      1011, 1080, 737, 1007, 1043, 874, 942
    ).map(_.toLong)
    assertEquals(results, expectedPoints)

    val score = results.sum.toInt
    assertEquals(score, 6694)
  }

  test("Calculate total points again") {
    val results: List[Long] = List(
      HundredHurdles.points(13.05),
      HighJump.points(1.67),
      ShotPut.points(12.87),
      TwoHundred.points(22.92),
      LongJump.points(6.45),
      Javelin.points(48.74),
      EightHundred.points(134.24)
    )

    val expectedPoints = List(
      1117, 818, 719, 1087, 991, 836, 904
    ).map(_.toLong)
    assertEquals(results, expectedPoints)

    val score = results.sum.toInt
    assertEquals(score, 6472)
  }

  test("Points helper for valid string") {
    val result = pointsHelper("13.05", HundredHurdles)
    assertEquals(result, 1117L)
  }

  test("Points helper for invalid string") {
    val result = pointsHelper("abc", HundredHurdles)
    assertEquals(result, 0L)
  }

  test("Points helper for invalid decimal") {
    val result = pointsHelper("12.13.05", HundredHurdles)
    assertEquals(result, 0L)
  }

  test("Middle Distance Points helper for only seconds") {
    val result = EightHundredPointsHelper("134.24")
    assertEquals(result, 904L)
  }

  test("Middle Distance Points helper for minutes and seconds") {
    val result = EightHundredPointsHelper("2:14.24")
    assertEquals(result, 904L)
  }

  test("Middle Distance Points helper for invalid separators") {
    val result = EightHundredPointsHelper("2:14:24")
    assertEquals(result, 0L)
  }

  test("Middle Distance Points helper for invalid string") {
    val result = EightHundredPointsHelper("2:abc")
    assertEquals(result, 0L)
  }

}
