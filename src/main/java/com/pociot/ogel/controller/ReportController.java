package com.pociot.ogel.controller;

import com.pociot.ogel.model.OEEReport;
import com.pociot.ogel.model.ProductionReport;
import com.pociot.ogel.service.ProductionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/reports")
public class ReportController {

  private final ProductionService productionService;

  //FIXME: fix cross origin value
  @GetMapping(path = "/production")
  @CrossOrigin(origins = "*")
  public Map<String, ProductionReport> get() {
    return productionService.generate24hProductionReport();
  }

  //FIXME: fix cross origin value
  @GetMapping(path = "/temperature")
  @CrossOrigin(origins = "*")
  public Map<String, String> getTemperature() {
    return productionService.generate24hTemperatureCondition();
  }

  //FIXME: fix cross origin value
  @GetMapping(path = "/oee")
  @CrossOrigin(origins = "*")
  public Map<String, OEEReport> getOEEReport() {
    return productionService.generate24hOEEReport();
  }
}
