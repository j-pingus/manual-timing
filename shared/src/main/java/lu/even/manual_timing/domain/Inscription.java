package lu.even.manual_timing.domain;

import java.io.Serializable;

public record Inscription(int event, int heat, int lane,
                          String name, String nation, String entrytime,
                          String clubcode, String agetext) implements Serializable {
}

