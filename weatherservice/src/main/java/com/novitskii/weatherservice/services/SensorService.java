package com.novitskii.weatherservice.services;

import com.novitskii.weatherservice.models.Sensor;
import com.novitskii.weatherservice.repositories.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class SensorService {
    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public Sensor getSensorByName(String name) {
        return sensorRepository.getSensorByName(name).orElse(null);
    }

    @Transactional
    public void save(Sensor sensor) {
        enrichSensor(sensor);
        sensorRepository.save(sensor);
    }

    private void enrichSensor(Sensor sensor) {
        sensor.setRegisteredAt(LocalDateTime.now());
    }
}
