package com.pociot.ogel.service.generators;

import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesProduction.VariableName;
import java.math.BigDecimal;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;

/**
 * Temperature indication generator. Given temperature calculated
 * condition of machine. Provides following values:
 * - good/green
 * - warning/orange - (warning temperature for no longer than 15 min)
 * - fatal/red
 */
@Slf4j
public class TemperatureReportGenerator {

  private final BigDecimal warningTemperature;
  private final BigDecimal fatalTemperature;

  private Duration warningDuration = Duration.ZERO;
  private boolean hasReachedFatalTemperature = false;

  private TemperatureReportGenerator(BigDecimal warningTemperature, BigDecimal fatalTemperature) {
    this.warningTemperature = warningTemperature;
    this.fatalTemperature = fatalTemperature;
  }

  /**
   * Default object initializer. Creates generator with warning temperature of 85
   * and fatal temperature of 100.
   * @return {@link TemperatureReportGenerator}
   */
  public static TemperatureReportGenerator withDefaults() {
    return new TemperatureReportGenerator(BigDecimal.valueOf(85), BigDecimal.valueOf(100));
  }

  /**
   * Populates {@link MachinesProduction} record of type CORE_TEMPERATURE.
   */
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

  /**
   * Returns condition indicator as {@link String}
   * @return {@link String}
   */
  public String getReport() {
    if (hasReachedFatalTemperature || warningDuration.toMinutes() > 15) {
      return "fatal/red";
    } else if (!warningDuration.isZero()) {
      return "warning/orange";
    }
    return "good/green";
  }
}
