package com.pociot.ogel.service.builders;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.service.builders.interfaces.TemperatureInitialized;
import com.pociot.ogel.service.generators.TemperatureReportGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TemperatureReportsBuilder implements TemperatureInitialized {

  private Map<String, TemperatureReportGenerator> generators;

  private TemperatureReportsBuilder() {
    generators = new HashMap<>();
  }

  public static TemperatureInitialized fromEvents(List<MachinesProduction> events) {
    TemperatureReportsBuilder builder = new TemperatureReportsBuilder();
    events.forEach(builder.addEvent());
    return builder;
  }

  private Consumer<MachinesProduction> addEvent() {
    return event -> getFromCacheOrCreate(event.getMachineName()).addRecord(event);
  }

  private TemperatureReportGenerator getFromCacheOrCreate(String machineName) {
    if (!generators.containsKey(machineName)) {
      generators.put(machineName, TemperatureReportGenerator.withDefaults());
    }
    return generators.get(machineName);
  }

  @Override
  public Map<String, String> build() {
    return generators.entrySet().stream().collect(Collectors.toMap(
        entry -> entry.getKey(),
        entry -> entry.getValue().getReport()
    ));
  }
}
