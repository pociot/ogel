package com.pociot.ogel.service.generators;

import com.pociot.ogel.model.MachinesRuntime;
import java.time.Duration;
import lombok.Getter;

/**
 * Helper class for calculating uptime/downtime.
 * Also calculates downtime percentage.
 */
@Getter
public class Runtime {
  private Duration downtime;
  private Duration uptime;
  private Duration totalTime;
  private MachinesRuntime lastRecord;

  public Runtime() {
    downtime = Duration.ZERO;
    uptime = Duration.ZERO;
    totalTime = Duration.ZERO;
  }

  public void addRuntime(boolean isRunning, Duration duration) {
    if (!isRunning) {
      uptime = uptime.plus(duration);
    } else {
      downtime = downtime.plus(duration);
    }
    totalTime = totalTime.plus(duration);
  }

  public void setLastRecord(MachinesRuntime lastRecord) {
    this.lastRecord = lastRecord;
  }

  public float getDowntimePercentage() {
    return 1f * downtime.toNanos() / totalTime.toNanos();
  }
}
