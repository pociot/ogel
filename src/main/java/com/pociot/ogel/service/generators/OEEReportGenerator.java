package com.pociot.ogel.service.generators;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesProduction.VariableName;
import com.pociot.ogel.model.MachinesRuntime;
import com.pociot.ogel.model.OEEReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OEEReportGenerator {

  private LocalDateTime dateTimeFrom = LocalDateTime.MAX;
  private LocalDateTime dateTimeTo = LocalDateTime.MIN;
  private Duration totalDuration = Duration.ZERO;

  private BigDecimal productionSum = BigDecimal.ZERO;
  private BigDecimal scrapSum = BigDecimal.ZERO;
  private Map<LocalDateTime, MachinesRuntime> runtimeCache = new TreeMap<>();

  private BigDecimal productionNormPerMinute;
  private BigDecimal availabilityNorm;

  private OEEReportGenerator(BigDecimal productionNormPerMinute,
      BigDecimal availabilityNorm) {
    this.productionNormPerMinute = productionNormPerMinute;
    this.availabilityNorm = availabilityNorm;
  }

  public static OEEReportGenerator withDefaults() {
    return new OEEReportGenerator(BigDecimal.valueOf(500), BigDecimal.valueOf(0.75F));
  }

  public void addRecord(MachinesProduction record) {
    if (VariableName.PRODUCTION.equals(record.getVariableName())) {
      productionSum = productionSum.add(record.getValue());
    } else if (VariableName.SCRAP.equals(record.getVariableName())) {
      scrapSum = scrapSum.add(record.getValue());
    } else {
      log.warn("Unsupported event type");
      return;
    }
    updateDateRange(record);
  }

  public void addRecord(MachinesRuntime record) {
    runtimeCache.put(record.getDateTime(), record);
  }

  public OEEReport getReport() {
    return OEEReport.builder()
        .performance(performance())
        .availability(availability())
        .quality(quality())
        .dateTimeFrom(dateTimeFrom)
        .dateTimeTo(dateTimeTo)
        .build();
  }

  private BigDecimal quality() {
    return productionSum.subtract(scrapSum)
        .setScale(4, RoundingMode.HALF_UP)
        .divide(productionSum, RoundingMode.HALF_UP);
  }

  private BigDecimal availability() {
    Runtime runtime = runtimeCache.values()
        .stream()
        .reduce(new Runtime(), reduceUptime(), combineUptime());
    return BigDecimal
        .valueOf(runtime.getUptime().toMinutes())
        .setScale(4, RoundingMode.HALF_UP)
        .divide(
            availabilityNorm.multiply(BigDecimal.valueOf(totalDuration.toMinutes())),
            RoundingMode.HALF_UP
        );
  }

  private BinaryOperator<Runtime> combineUptime() {
    return (runtime1, runtime2) -> {
      runtime1.addRuntime(false, runtime2.getDowntime());
      runtime1.addRuntime(true, runtime2.getUptime());
      return runtime1;
    };
  }

  private BiFunction<Runtime, MachinesRuntime, Runtime> reduceUptime() {
    return (runtime1, machinesRuntime) -> {
      if (runtime1.getLastRecord() != null) {
        Duration duration = Duration.between(runtime1.getLastRecord().getDateTime(), machinesRuntime.getDateTime());
        runtime1.addRuntime(machinesRuntime.getIsRunning(), duration);
      }
      runtime1.setLastRecord(machinesRuntime);
      return runtime1;
    };
  }

  private BigDecimal performance() {
    BigDecimal norm = productionNormPerMinute
        .multiply(BigDecimal.valueOf(totalDuration.toMinutes()));
    return productionSum
        .setScale(4, RoundingMode.HALF_UP)
        .divide(norm, RoundingMode.HALF_UP);
  }

  private void updateDateRange(MachinesProduction record) {
    if (record.getDateTimeFrom().isBefore(this.dateTimeFrom)) {
      this.dateTimeFrom = record.getDateTimeFrom();
      this.totalDuration = Duration.between(this.dateTimeFrom, this.dateTimeTo);
    }
    if (record.getDateTimeTo().isAfter(this.dateTimeTo)) {
      this.dateTimeTo = record.getDateTimeTo();
      this.totalDuration = Duration.between(this.dateTimeFrom, this.dateTimeTo);
    }
  }
}
