package com.novitskii.weatherservice.repositories;

import com.novitskii.weatherservice.models.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> getSensorByName(String name);
}
