package lu.even.meet_manager.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceConverterTest {
  @ParameterizedTest
  @CsvSource({
    "25m,25,1,false",
    "50m,25,1,false",
    "100m,25,2,false",
    "200m,25,4,false",
    "225m,25,5,false",
    "400m,25,8,false",
    "4 x 100m,25,8,false",
    "50m,50,1,true",
    "100m,50,2,true",
    "200m,50,4,true",
    "400m,50,8,true",
    "4 x 100m,50,8,true"
  })
  public void testConvert(String distance, int poolLength, int expected,boolean bothEnds) {
    assertEquals(expected, DistanceConverter.getIntermediates(distance, poolLength,bothEnds));
  }
}
