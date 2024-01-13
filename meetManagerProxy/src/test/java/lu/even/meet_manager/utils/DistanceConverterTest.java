package lu.even.meet_manager.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceConverterTest {

  @ParameterizedTest
  @CsvSource({
    "25m,25",
    "50m,50",
    "100m,100",
    "200m,200",
    "225m,225",
    "400m,400",
    "4 x 100m,400"
  })
  public void testComputeDistance(String distance, int expectedMeters) {
    assertEquals(expectedMeters, DistanceConverter.computeDistance(distance));
  }

  @ParameterizedTest
  @CsvSource({
    "25,25,false,1",
    "50,25,false,1",
    "100,25,false,2",
    "200,25,false,4",
    "225,25,false,5",
    "400,25,false,8",
    "50,50,true,1",
    "100,50,true,2",
    "200,50,true,4",
    "400,50,true,8",
  })
  public void testComputeTimingAmount(int distance, int poolLength, boolean bothEnds, int expected) {
    assertEquals(expected, DistanceConverter.computeTimingAmount(
      distance, poolLength, bothEnds));
  }

  @ParameterizedTest
  @CsvSource({
    "25m,25,false,25",
    "50m,25,false,50",
    "100m,25,false,50-100",
    "200m,25,false,50-100-150-200",
    "225m,25,false,50-100-150-200-225",
    "400m,25,false,50-100-150-200-250-300-350-400",
    "50m,50,true,50",
    "100m,50,true,50-100",
    "200m,50,true,50-100-150-200",
    "4 x 100m,50,true,50-100-150-200-250-300-350-400",
    "50m,50,false,50",
    "100m,50,false,100",
    "200m,50,false,100-200",
    "400m,50,false,100-200-300-400",
  })
  public void testComputeTimingDistances(String distanceDescription, int poolLength, boolean bothEnds, String expected) {
    int[] distances = DistanceConverter.computeTimingDistances(distanceDescription, poolLength, bothEnds);
    String actual = null;
    for (int distance : distances) {
      if (actual == null) {
        actual = "" + distance;
      } else {
        actual += "-" + distance;
      }
    }
    assertEquals(expected, actual);
  }
}
