package lu.even.meet_manager.utils;

import lu.even.manual_timing.domain.SwimmingEvent;
import lu.even.meet_manager.domain.Event;
import lu.even.meet_manager.domain.Events;
import lu.even.meet_manager.domain.Gender;
import lu.even.meet_manager.domain.Stroke;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventConverter {
  public static List<SwimmingEvent> convert(Events events, int poolLength, boolean bothends) {
    return events.getDynamicValues().values().stream()
      .filter(e -> !e.heats().isEmpty())
      .map(e -> new SwimmingEvent(
        toNumber(e.number()),
        e.heats().size(),
        e.isrelay(),
        describe(e),
        e.time(),
        e.date(),
        DistanceConverter.computeTimingDistances(e.distance(), poolLength, bothends)
      ))
      .sorted(Comparator.comparingInt(SwimmingEvent::id))
      .collect(Collectors.toList());
  }

  private static String describe(Event e) {
    return
      (e.isrelay() ? "Relay " : "") +
        e.distance() + " " +
        decodeStroke(e.stroke()) + " " +
        decodeGender(e.gender());
  }

  private static String decodeGender(String gender) {
    return Gender.getByCode(gender).getDescription();
  }

  private static String decodeStroke(String stroke) {
    return Stroke.getByCode(stroke).getDescription();
  }

  private static int toNumber(String number) {
    if (number != null)
      return Integer.parseInt(number);
    return -1;
  }
}
