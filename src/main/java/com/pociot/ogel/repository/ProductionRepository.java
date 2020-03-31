package com.pociot.ogel.repository;

import com.pociot.ogel.model.MachinesProduction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<MachinesProduction, Integer> {

  List<MachinesProduction> findAllByDateTimeFromBetweenAndVariableNameIsIn(LocalDateTime start, LocalDateTime end, List<String> variableNames);

  @Query(value = "select max(p.dateTimeFrom) from production p where p.variableName in ?1")
  LocalDateTime findLastRecordDateTime(List<String> names);
}
