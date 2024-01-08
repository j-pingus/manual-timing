package lu.even.meet_manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record Entry(String nation, String entrytime, String nametext, int lane, String clubcode, String gender,String agetext) {
}
