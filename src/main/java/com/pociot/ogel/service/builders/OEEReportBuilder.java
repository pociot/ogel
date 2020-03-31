package com.pociot.ogel.service.builders;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesRuntime;
import com.pociot.ogel.model.OEEReport;
import com.pociot.ogel.service.generators.OEEReportGenerator;
import com.pociot.ogel.service.builders.interfaces.StatisticsInitialized;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OEEReportBuilder implements StatisticsInitialized {

  private Map<String, OEEReportGenerator> generators;

  private OEEReportBuilder() {
    this.generators = new HashMap<>();
  }

  public static StatisticsInitialized fromEvents(List<MachinesProduction> production,
      List<MachinesRuntime> runtime) {
    OEEReportBuilder generator = new OEEReportBuilder();
    production.forEach(generator.addProduction());
    runtime.forEach(generator.addRuntime());
    return generator;
  }

  private Consumer<MachinesRuntime> addRuntime() {
    return machinesRuntime ->
        getFromCacheOrCreate(machinesRuntime.getMachineName())
            .addRecord(machinesRuntime);
  }

  private Consumer<MachinesProduction> addProduction() {
    return machinesProduction ->
        getFromCacheOrCreate(machinesProduction.getMachineName())
            .addRecord(machinesProduction);
  }

  private OEEReportGenerator getFromCacheOrCreate(String machineName) {
    if (!generators.containsKey(machineName)) {
      generators.put(machineName, OEEReportGenerator.withDefaults());
    }
    return generators.get(machineName);
  }

  @Override
  public Map<String, OEEReport> build() {
    return generators.entrySet().stream().collect(Collectors.toMap(
        entry -> entry.getKey(),
        entry -> entry.getValue().getReport()
    ));
  }
}
