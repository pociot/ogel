package com.pociot.ogel.service.builders.interfaces;

import com.pociot.ogel.model.ProductionReport;
import java.util.Map;

public interface ProductionInitialized {
  Map<String, ProductionReport> build();
}