package lu.even.meet_manager.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.even.meet_manager.domain.Events;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class EventConverterTest {
  @Test
  public void convertToFile() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    var events = mapper.readValue(EventConverter.class.getResource("/events.json"), Events.class);
    var converted = EventConverter.convert(events, 50, true);
    mapper.writeValue(new File("./target/events.json"), converted);
  }
}
