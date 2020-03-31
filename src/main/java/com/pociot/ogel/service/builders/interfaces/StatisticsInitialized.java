package com.pociot.ogel.service.builders.interfaces;

import com.pociot.ogel.model.OEEReport;
import java.util.Map;

public interface StatisticsInitialized {
  Map<String, OEEReport> build();
}
