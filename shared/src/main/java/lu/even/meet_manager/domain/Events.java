package lu.even.meet_manager.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Events {
    Date lastupdate;
    @JsonAnySetter
    Map<String, Event> dynamicValues;

    public Events() {
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    @Override
    public String toString() {
        return "Events{" +
                "lastupdate=" + lastupdate +
                ", dynamicValues=" + dynamicValues +
                '}';
    }

    public Map<String, Event> getDynamicValues() {
        return dynamicValues;
    }
}
