package com.pociot.ogel.controller;

import com.pociot.ogel.model.OEEReport;
import com.pociot.ogel.model.ProductionReport;
import com.pociot.ogel.service.ProductionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/reports")
public class ReportController {

  private final ProductionService productionService;

  @GetMapping(path = "/production")
  @CrossOrigin(origins = {"http://localhost:3000", "https://ogel.herokuapp.com"})
  public Map<String, ProductionReport> production() {
    log.info("Production report requested.");
    return productionService.generate24hProductionReport();
  }

  @GetMapping(path = "/temperature")
  @CrossOrigin(origins = {"http://localhost:3000", "https://ogel.herokuapp.com"})
  public Map<String, String> temperature() {
    log.info("Temperature report requested.");
    return productionService.generate24hTemperatureCondition();
  }

  @GetMapping(path = "/oee")
  @CrossOrigin(origins = {"http://localhost:3000", "https://ogel.herokuapp.com"})
  public Map<String, OEEReport> oee() {
    log.info("OEE report requested.");
    return productionService.generate24hOEEReport();
  }
}
