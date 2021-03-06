package com.pociot.ogel.service.builders;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesRuntime;
import com.pociot.ogel.model.ProductionReport;
import com.pociot.ogel.service.builders.interfaces.ProductionInitialized;
import com.pociot.ogel.service.generators.ProductionReportGenerator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Generates OEE report for every machine which has production and runtime events.
 */
public class ProductionReportsBuilder implements ProductionInitialized {

  private final Map<String, ProductionReportGenerator> generators;

  private ProductionReportsBuilder() {
    generators = new HashMap<>();
  }

  /**
   * Report builder initializer. Takes list of production and runtime events
   * and populates across generators.
   * @param production - uncategorized production events
   * @param runtime - uncategorized runtime events
   * @return {@link ProductionReportsBuilder} with .build() method.
   */
  public static ProductionInitialized fromEvents(List<MachinesProduction> production,
      List<MachinesRuntime> runtime) {
    ProductionReportsBuilder generator = new ProductionReportsBuilder();
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

  private ProductionReportGenerator getFromCacheOrCreate(String machineName) {
    if (!generators.containsKey(machineName)) {
      generators.put(machineName, ProductionReportGenerator.withDefaults());
    }
    return generators.get(machineName);
  }

  /**
   * Takes generators map and creates map of production reports.
   * @return Map of machine names and associated reports.
   */
  @Override
  public Map<String, ProductionReport> build() {
    return generators.entrySet().stream().collect(Collectors.toMap(
        entry -> entry.getKey(),
        entry -> entry.getValue().getReport()
    ));
  }
}
