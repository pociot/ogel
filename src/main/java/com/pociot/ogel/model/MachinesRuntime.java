package com.pociot.ogel.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Type;

@Data
@Entity(name = "runtime")
public class MachinesRuntime {

  @Id
  private int id;

  @Column(name = "machine_name")
  private String machineName;

  @Column(name = "datetime")
  private LocalDateTime dateTime;

  @Column(name = "isrunning", columnDefinition = "TINYINT")
  @Type(type = "org.hibernate.type.NumericBooleanType")
  private Boolean isRunning;
}
