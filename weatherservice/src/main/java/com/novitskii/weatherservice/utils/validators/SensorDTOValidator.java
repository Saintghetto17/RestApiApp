package com.novitskii.weatherservice.utils.validators;

import com.novitskii.weatherservice.DTO.SensorDTO;
import com.novitskii.weatherservice.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SensorDTOValidator implements Validator {
    private final SensorService sensorService;

    @Autowired
    public SensorDTOValidator(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SensorDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SensorDTO sensor = (SensorDTO) target;
        if (sensorService.getSensorByName(sensor.getName()) != null) {
            errors.rejectValue("name", "The sensor with this name already exists");
        }
    }
}
