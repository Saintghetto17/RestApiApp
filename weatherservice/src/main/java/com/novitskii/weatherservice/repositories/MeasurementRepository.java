package com.novitskii.weatherservice.repositories;

import com.novitskii.weatherservice.models.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Integer> {
    Long countByRainingTrue();
}
