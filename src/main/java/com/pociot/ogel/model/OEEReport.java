package com.pociot.ogel.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OEEReport {
  private LocalDateTime dateTimeFrom;
  private LocalDateTime dateTimeTo;
  private BigDecimal performance;
  private BigDecimal availability;
  private BigDecimal quality;
  private BigDecimal oee;

  public BigDecimal getOee() {
    return performance
        .multiply(availability)
        .multiply(quality)
        .setScale(4, RoundingMode.HALF_UP);
  }
}
