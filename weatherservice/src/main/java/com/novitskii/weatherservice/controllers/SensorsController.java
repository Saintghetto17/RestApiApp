package com.novitskii.weatherservice.controllers;

import com.novitskii.weatherservice.DTO.SensorDTO;
import com.novitskii.weatherservice.models.Sensor;
import com.novitskii.weatherservice.services.SensorService;
import com.novitskii.weatherservice.utils.validators.SensorDTOValidator;
import com.novitskii.weatherservice.utils.errors.SensorErrorResponse;
import com.novitskii.weatherservice.utils.exceptions.SensorNotCreatedException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDate;

@RestController
@RequestMapping("/sensors")
public class SensorsController {

    private final SensorService sensorService;
    private final ModelMapper modelMapper;
    private final SensorDTOValidator validator;

    @Autowired
    public SensorsController(SensorService sensorService, ModelMapper modelMapper, SensorDTOValidator validator) {
        this.sensorService = sensorService;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registerSensor(@RequestBody @Valid SensorDTO sensorDTO,
                                                     BindingResult bindingResult) {
        validator.validate(sensorDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errorMsg.append(error.getField())
                        .append("---")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new SensorNotCreatedException(errorMsg.toString());
        }
        sensorService.save(convertToSensor(sensorDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    private Sensor convertToSensor(SensorDTO sensorDTO) {
        return modelMapper.map(sensorDTO, Sensor.class);
    }

    private SensorDTO convertToSensorDTO(Sensor sensor) {
        return modelMapper.map(sensor, SensorDTO.class);
    }

    @ExceptionHandler
    private ResponseEntity<SensorErrorResponse> handleException(SensorNotCreatedException e) {
        SensorErrorResponse sensorErrorResponse = new SensorErrorResponse(e.getMessage(), LocalDate.now());
        return new ResponseEntity<>(sensorErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    ResponseEntity<SensorErrorResponse> handleException(RestClientResponseException ex) {
        SensorErrorResponse sensorErrorResponse = new SensorErrorResponse(ex.getMessage(), LocalDate.now());
        return new ResponseEntity<>(sensorErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
