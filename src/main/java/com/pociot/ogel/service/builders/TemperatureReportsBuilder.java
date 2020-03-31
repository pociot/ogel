package com.pociot.ogel.service.builders;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.service.builders.interfaces.TemperatureInitialized;
import com.pociot.ogel.service.generators.TemperatureReportGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Generates indication of machines status based on their temperature.
 */
public class TemperatureReportsBuilder implements TemperatureInitialized {

  private Map<String, TemperatureReportGenerator> generators;

  private TemperatureReportsBuilder() {
    generators = new HashMap<>();
  }

  /**
   * Report builder initializer. Takes list of production and runtime events
   * and populates across generators.
   * @param events - uncategorized production events
   * @return {@link TemperatureReportsBuilder} with .build() method.
   */
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

  /**
   * Takes generators map and creates map of temperature reports.
   * @return Map of machine names and associated reports.
   */
  @Override
  public Map<String, String> build() {
    return generators.entrySet().stream().collect(Collectors.toMap(
        entry -> entry.getKey(),
        entry -> entry.getValue().getReport()
    ));
  }
}
