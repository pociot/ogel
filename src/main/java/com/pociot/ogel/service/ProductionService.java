package com.pociot.ogel.service;

import static com.pociot.ogel.model.MachinesProduction.VariableName.CORE_TEMPERATURE;
import static com.pociot.ogel.model.MachinesProduction.VariableName.PRODUCTION;
import static com.pociot.ogel.model.MachinesProduction.VariableName.SCRAP;
import static java.time.temporal.ChronoUnit.HOURS;

import com.google.common.collect.Lists;
import com.pociot.ogel.model.MachinesProduction;
import com.pociot.ogel.model.MachinesRuntime;
import com.pociot.ogel.model.OEEReport;
import com.pociot.ogel.model.ProductionReport;
import com.pociot.ogel.repository.ProductionRepository;
import com.pociot.ogel.repository.RuntimeRepository;
import com.pociot.ogel.service.builders.OEEReportBuilder;
import com.pociot.ogel.service.builders.ProductionReportsBuilder;
import com.pociot.ogel.service.builders.TemperatureReportsBuilder;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductionService {

  private final ProductionRepository productionRepository;
  private final RuntimeRepository runtimeRepository;

  private List<String> productionVariableNames = Lists.newArrayList(PRODUCTION, SCRAP);
  private List<String> temperatureVariableName = Collections.singletonList(CORE_TEMPERATURE);

  public ProductionService(ProductionRepository productionRepository,
      RuntimeRepository runtimeRepository) {
    this.productionRepository = productionRepository;
    this.runtimeRepository = runtimeRepository;
  }

  public Map<String, ProductionReport> generate24hProductionReport() {
    return this.generate24hProductionReport(productionRepository.findLastRecordDateTime(productionVariableNames));
  }

  public Map<String, ProductionReport> generate24hProductionReport(LocalDateTime startTime) {
    log.info("Requested 24h production report for {}", startTime.toString());
    return ProductionReportsBuilder
        .fromEvents(getProductionRecordsFor24h(startTime), getRuntimeRecordsFor24h(startTime))
        .build();
  }

  public Map<String, String> generate24hTemperatureCondition() {
    return this.generate24hTemperatureCondition(productionRepository.findLastRecordDateTime(temperatureVariableName));
  }

  public Map<String, String> generate24hTemperatureCondition(LocalDateTime startTime) {
    log.info("Requested 24h temperature report for {}", startTime.toString());
    return TemperatureReportsBuilder
        .fromEvents(getTemperatureRecordsFor24h(startTime))
        .build();
  }

  public Map<String, OEEReport> generate24hOEEReport() {
    return this.generate24hOEEReport(productionRepository.findLastRecordDateTime(productionVariableNames));
  }

  public Map<String, OEEReport> generate24hOEEReport(LocalDateTime dateTime) {
    log.info("Requested 24h OEE report for {}", dateTime.toString());
    return OEEReportBuilder
        .fromEvents(getProductionRecordsFor24h(dateTime), getRuntimeRecordsFor24h(dateTime))
        .build();
  }

  private List<MachinesProduction> getProductionRecordsFor24h(LocalDateTime dateTime) {
    return productionRepository.findAllByDateTimeFromBetweenAndVariableNameIsIn(
        dateTime.minusHours(23).truncatedTo(HOURS),
        dateTime,
        productionVariableNames
    );
  }

  private List<MachinesRuntime> getRuntimeRecordsFor24h(LocalDateTime dateTime) {
    return runtimeRepository.findAllByDateTimeBetween(dateTime.minusDays(1), dateTime);
  }

  private List<MachinesProduction> getTemperatureRecordsFor24h(LocalDateTime dateTime) {
    return productionRepository.findAllByDateTimeFromBetweenAndVariableNameIsIn(
        dateTime.minusHours(23).truncatedTo(HOURS),
        dateTime,
        temperatureVariableName
    );
  }
}
