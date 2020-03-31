package com.pociot.ogel.service.generators;

import static java.time.temporal.ChronoUnit.HOURS;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesProduction.VariableName;
import com.pociot.ogel.model.MachinesRuntime;
import com.pociot.ogel.model.ProductionReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import lombok.extern.slf4j.Slf4j;

/**
 * Production report generator. Based on production and runtime events for single
 * machine this class generates report with values:
 * - Total net production
 * - Scrap percentage
 * - Downtime percentage
 * - Hourly net production
 */
@Slf4j
public class ProductionReportGenerator {
  private LocalDateTime dateTimeFrom = LocalDateTime.MAX;
  private LocalDateTime dateTimeTo = LocalDateTime.MIN;
  private Map<LocalDateTime, MachinesRuntime> downtime = new TreeMap<>();

  private Map<LocalDateTime, BigDecimal> productionByHour = new TreeMap<>();
  private BigDecimal productionSum = BigDecimal.ZERO;
  private BigDecimal scrapSum = BigDecimal.ZERO;

  private ProductionReportGenerator() {
    // hidden constructor
  }

  /**
   * Default object initializer.
   * @return {@link ProductionReportGenerator}
   */
  public static ProductionReportGenerator withDefaults() {
    return new ProductionReportGenerator();
  }

  /**
   * Populates {@link MachinesProduction} record of type PRODUCTION or SCRAP.
   */
  public void addRecord(MachinesProduction record) {
    LocalDateTime hour = record.getDateTimeFrom().truncatedTo(HOURS);
    if (VariableName.PRODUCTION.equals(record.getVariableName())) {
      productionSum = productionSum.add(record.getValue());
      addProductionByHour(record, hour);
    } else if (VariableName.SCRAP.equals(record.getVariableName())) {
      scrapSum = scrapSum.add(record.getValue());
      subtractProductionByHour(record, hour);
    } else {
      log.warn("Unsupported event type");
      return;
    }
    updateDateRange(record);
  }

  /**
   * Populates {@link MachinesRuntime} record.
   */
  public void addRecord(MachinesRuntime record) {
    downtime.put(record.getDateTime(), record);
  }

  /**
   * Creates report for single machine based on previously added events.
   *
   * @return {@link ProductionReport}
   */
  public ProductionReport getReport() {
    return ProductionReport.builder()
        .netProduction(totalNetProduction())
        .scrapPercentage(scrapPercentage())
        .downtime(totalDowntime())
        .netProductionByHour(productionByHour)
        .dateTimeFrom(dateTimeFrom)
        .dateTimeTo(dateTimeTo)
        .build();
  }

  private void addProductionByHour(MachinesProduction record, LocalDateTime hour) {
    applyProductionByHour(
        hour,
        existingValue -> existingValue.add(record.getValue())
    );
  }

  private void subtractProductionByHour(MachinesProduction record, LocalDateTime hour) {
    applyProductionByHour(
        hour,
        existingValue -> existingValue.subtract(record.getValue())
    );
  }

  private void applyProductionByHour(LocalDateTime hour, UnaryOperator<BigDecimal> function) {
    productionByHour
        .put(hour, function.apply(productionByHour.getOrDefault(hour, BigDecimal.ZERO)));
  }

  private float totalDowntime() {
    return downtime.values()
        .stream()
        .reduce(new Runtime(), reduceDowntime(), combineDowntime())
        .getDowntimePercentage();
  }

  private BinaryOperator<Runtime> combineDowntime() {
    return (runtime1, runtime2) -> {
      runtime1.addRuntime(false, runtime2.getDowntime());
      runtime1.addRuntime(true, runtime2.getUptime());
      return runtime1;
    };
  }

  private BiFunction<Runtime, MachinesRuntime, Runtime> reduceDowntime() {
    return (runtime1, machinesRuntime) -> {
      if (runtime1.getLastRecord() != null) {
        Duration duration = Duration.between(runtime1.getLastRecord().getDateTime(), machinesRuntime.getDateTime());
        runtime1.addRuntime(machinesRuntime.getIsRunning(), duration);
      }
      runtime1.setLastRecord(machinesRuntime);
      return runtime1;
    };
  }

  private BigDecimal scrapPercentage() {
    return this.scrapSum
        .setScale(4, RoundingMode.HALF_UP)
        .divide(this.productionSum, RoundingMode.HALF_UP);
  }

  private BigDecimal totalNetProduction() {
    return productionSum.subtract(this.scrapSum);
  }

  private void updateDateRange(MachinesProduction record) {
    if (record.getDateTimeFrom().isBefore(this.dateTimeFrom)) {
      this.dateTimeFrom = record.getDateTimeFrom();
    }
    if (record.getDateTimeTo().isAfter(this.dateTimeTo)) {
      this.dateTimeTo = record.getDateTimeTo();
    }
  }
}
