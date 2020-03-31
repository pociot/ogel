package com.pociot.ogel.service.generators;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesProduction.VariableName;
import java.math.BigDecimal;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemperatureReportGenerator {

  private final BigDecimal warningTemperature;
  private final BigDecimal fatalTemperature;

  private Duration warningDuration;
  private boolean hasReachedFatalTemperature;

  private TemperatureReportGenerator(BigDecimal warningTemperature, BigDecimal fatalTemperature) {
    this.warningTemperature = warningTemperature;
    this.fatalTemperature = fatalTemperature;
    warningDuration = Duration.ZERO;
    hasReachedFatalTemperature = false;
  }

  public static TemperatureReportGenerator withDefaults() {
    return new TemperatureReportGenerator(BigDecimal.valueOf(85), BigDecimal.valueOf(100));
  }

  public void addRecord(MachinesProduction record) {
    if (VariableName.CORE_TEMPERATURE.equals(record.getVariableName())) {
      if (fatalTemperature.compareTo(record.getValue()) < 0) {
        log.debug("Temperature over 100 - {}", record);
        hasReachedFatalTemperature = true;
        warningDuration = warningDuration.plus(
            Duration.between(record.getDateTimeFrom(), record.getDateTimeTo())
        );
      } else if (warningTemperature.compareTo(record.getValue()) < 0) {
        log.debug("Temperature over 85 - {}", record);
        warningDuration = warningDuration.plus(
            Duration.between(record.getDateTimeFrom(), record.getDateTimeTo())
        );
      }
    } else {
      log.warn("Unsupported event type received.");
    }
  }

  public String getReport() {
    if (hasReachedFatalTemperature || warningDuration.toMinutes() > 15) {
      return "fatal/red";
    } else if (!warningDuration.isZero()) {
      return "warning/orange";
    }
    return "good/green";
  }
}
