package com.pociot.ogel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductionReport {

  private LocalDateTime dateTimeFrom;
  private LocalDateTime dateTimeTo;
  private BigDecimal netProduction;
  private BigDecimal scrapPercentage;
  private float downtime;
  private Map<LocalDateTime, BigDecimal> netProductionByHour;
}
