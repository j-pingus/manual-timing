package lu.even.meet_manager.utils;

public class DistanceConverter {
  public static int getIntermediates(String distance, int poolLength, boolean bothEnds){
    var split = distance.split("x");
    int distanceInMeters = Integer.parseInt(split[split.length-1].split("m")[0].trim());
    int multiplier =  split.length==1?1:Integer.parseInt(split[0].trim());
    int x = distanceInMeters * multiplier /poolLength;
    int measures=bothEnds?1:2;
    int odd = x % measures;
    return ((x-odd)/measures)+odd;
  }
}
