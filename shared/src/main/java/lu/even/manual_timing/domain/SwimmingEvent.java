package lu.even.manual_timing.domain;

public record SwimmingEvent(int id, int heats, boolean relay, String description, String time, String date,
                            int[] distances) {
}
