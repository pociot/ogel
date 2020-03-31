package com.pociot.ogel.repository;

import com.pociot.ogel.model.MachinesRuntime;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RuntimeRepository extends JpaRepository<MachinesRuntime, Integer> {

  List<MachinesRuntime> findAllByDateTimeBetween(LocalDateTime start, LocalDateTime end);

  @Query(value = "select max(r.dateTime) from runtime r")
  LocalDateTime findLastRecordDateTime();
}
