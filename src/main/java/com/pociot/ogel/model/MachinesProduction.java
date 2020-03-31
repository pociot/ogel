package com.pociot.ogel.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import lombok.ToString;

@Data
@Entity(name = "production")
@ToString
public class MachinesProduction {

  @Id
  private int id;

  @Column(name = "machine_name")
  private String machineName;

  @Column(name = "variable_name")
  private String variableName;

  @Column(name = "datetime_from")
  private LocalDateTime dateTimeFrom;

  @Column(name = "datetime_to")
  private LocalDateTime dateTimeTo;

  @Column(name = "value")
  private BigDecimal value;

  public static class VariableName {

    private VariableName() {
      //hidden public constructor
    }

    public static final String PRODUCTION = "PRODUCTION";
    public static final String SCRAP = "SCRAP";
    public static final String CORE_TEMPERATURE = "CORE TEMPERATURE";
  }
}
