package lu.even.meet_manager.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DistanceConverterTest {
  @ParameterizedTest
  @CsvSource({
    "50m,25,1",
    "100m,25,3",
    "200m,25,7",
    "400m,25,15",
    "4 x 100m,25,15",
    "50m,50,0",
    "100m,50,1",
    "200m,50,3",
    "400m,50,7",
    "4 x 100m,50,7"
  })
  public void testConvert(String distance, int poolLength, int expected) {
    assertEquals(expected, DistanceConverter.getIntermediates(distance, poolLength));
  }
}
