package lu.even.meet_manager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public record HeatDetails(List<Entry> entries) {
}
