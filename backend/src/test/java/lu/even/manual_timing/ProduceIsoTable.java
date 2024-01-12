package lu.even.manual_timing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lu.even.manual_timing.domain.Inscription;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ProduceIsoTable {
  @Test
  public void produceCountry() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    var map = Arrays.stream(Locale.getISOCountries())
      .map(c -> new CountryCodes(c.toUpperCase(), new Locale("", c).getISO3Country()))
      //.filter(cc -> !cc.iso2.equals(cc.iso3.substring(0, 2)))
      .map(cc -> Arrays.asList(cc.iso3, cc.iso2))
      .collect(Collectors.toList());
    System.out.println(mapper.writeValueAsString(map));
  }

  @Test
  public void listAllCountries() throws IOException {
    File input = new File("../inscriptions.json").getAbsoluteFile();
    if (input.exists()) {
      var inscriptions = new ObjectMapper().readValue(input, Inscription[].class);
      Map<String, Inscription> uniqueNations = new HashMap<>();
      Arrays.stream(inscriptions)
        .forEach(i -> {
          if (!uniqueNations.containsKey(i.nation())) {
            uniqueNations.put(i.nation(), i);
          }
        });
      uniqueNations.entrySet().stream()
        .sorted(Comparator.comparingInt(c -> c.getValue().event()))
        .forEach((e)-> System.out.println(e.getKey()+":"+e.getValue()));
    }
  }

  private record CountryCodes(String iso2, String iso3) {
  }
}
