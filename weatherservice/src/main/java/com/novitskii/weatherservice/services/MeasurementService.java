package com.novitskii.weatherservice.services;

import com.novitskii.weatherservice.models.Measurement;
import com.novitskii.weatherservice.repositories.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementService {
    private final MeasurementRepository measurementRepository;
    private final SensorService sensorService;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository, SensorService sensorService) {
        this.measurementRepository = measurementRepository;
        this.sensorService = sensorService;
    }

    @Transactional
    public void save(Measurement measurement) {
        enrichMeasurement(measurement);
        measurementRepository.save(measurement);
    }

    private void enrichMeasurement(Measurement measurement) {
        measurement.setMeasuredAt(LocalDateTime.now());
        measurement.setSensor(sensorService.getSensorByName(measurement.getSensor().getName()));
    }

    public List<Measurement> findAll() {
        return measurementRepository.findAll();
    }

    public Long countByRainingTrue() {
        return measurementRepository.countByRainingTrue();
    }
}
