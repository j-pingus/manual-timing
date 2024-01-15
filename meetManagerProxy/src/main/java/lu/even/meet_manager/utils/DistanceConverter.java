package lu.even.meet_manager.utils;

public class DistanceConverter {
  public static int computeDistance(String distanceDescription) {
    var split = distanceDescription.split("x");
    int distanceInMeters = Integer.parseInt(split[split.length - 1].split("m")[0].trim());
    int multiplier = split.length == 1 ? 1 : Integer.parseInt(split[0].trim());
    return distanceInMeters * multiplier;
  }

  public static int computeTimingAmount(int distance, int poolLength, boolean bothEnds) {
    int x = distance / poolLength;
    int measures = bothEnds ? 1 : 2;
    int odd = x % measures;
    return ((x - odd) / measures) + odd;
  }

  public static int[] computeTimingDistances(String distanceDescription, int poolLength, boolean bothEnds) {
    int distance = computeDistance(distanceDescription);
    int amount = computeTimingAmount(distance, poolLength, bothEnds);
    int timingDistance = bothEnds ? poolLength : 2 * poolLength;
    int[] ret = new int[amount];
    ret[0] = timingDistance;
    for (int i = 1; i < amount - 1; i++) {
      ret[i] = ret[i - 1] + timingDistance;
    }
    ret[amount - 1] = distance;
    return ret;
  }
}
