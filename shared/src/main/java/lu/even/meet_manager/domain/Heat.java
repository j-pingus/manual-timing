package lu.even.meet_manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public record Heat(String code,String id, String time) {
}
