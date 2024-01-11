package lu.even.meet_manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public record Event(String number, String gender, String stroke, String distance,boolean isrelay, List<Heat> heats) {

}
