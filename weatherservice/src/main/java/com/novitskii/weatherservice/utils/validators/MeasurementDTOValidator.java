package com.novitskii.weatherservice.utils.validators;

import com.novitskii.weatherservice.DTO.MeasurementDTO;
import com.novitskii.weatherservice.services.MeasurementService;
import com.novitskii.weatherservice.services.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class MeasurementDTOValidator implements Validator {

    private final MeasurementService measurementService;
    private final SensorService sensorService;

    @Autowired
    public MeasurementDTOValidator(MeasurementService measurementService, SensorService sensorService) {
        this.measurementService = measurementService;
        this.sensorService = sensorService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return MeasurementDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MeasurementDTO measurementDTO = (MeasurementDTO) target;

        if (measurementDTO.getValue() < -100 || measurementDTO.getValue() > 100) {
            errors.rejectValue("value", "Temperature should be between -100 *C and 100 *C");
        }
        if (sensorService.getSensorByName(measurementDTO.getSensor()) == null) {
            errors.rejectValue("sensorName", "Sensor does not exist");
        }
    }
}
