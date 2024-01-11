import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lu.even.meet_manager.domain.Events;
import lu.even.meet_manager.domain.HeatDetails;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class TestParsing {
    @Test
    public void parseEvents() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream is = TestParsing.class.getResourceAsStream("/events.json");
        Events ev = objectMapper.reader().readValue(is, Events.class);
        System.out.println(ev);
    }
    @Test
    public void parseHeat() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream is = TestParsing.class.getResourceAsStream("/heat.json");
        HeatDetails heat = objectMapper.reader().readValue(is, HeatDetails.class);
        System.out.println(heat);
    }
    @Test
    public void parseHeatRelay() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream is = TestParsing.class.getResourceAsStream("/heatRelay.json");
        HeatDetails heat = objectMapper.reader().readValue(is, HeatDetails.class);
        System.out.println(heat);
    }
}
