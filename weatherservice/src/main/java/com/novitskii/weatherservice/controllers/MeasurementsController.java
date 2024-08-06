package com.novitskii.weatherservice.controllers;

import com.novitskii.weatherservice.DTO.MeasurementDTO;
import com.novitskii.weatherservice.models.Measurement;
import com.novitskii.weatherservice.services.MeasurementService;
import com.novitskii.weatherservice.services.SensorService;
import com.novitskii.weatherservice.utils.validators.MeasurementDTOValidator;
import com.novitskii.weatherservice.utils.errors.MeasurementErrorResponse;
import com.novitskii.weatherservice.utils.exceptions.MeasurementNotAddedException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/measurements")
public class MeasurementsController {

    private final MeasurementService measurementService;
    private final ModelMapper modelMapper;
    private final MeasurementDTOValidator measurementDTOValidator;
    private final SensorService sensorService;
    @Autowired
    public MeasurementsController(MeasurementService measurementService, ModelMapper modelMapper,
                                  MeasurementDTOValidator measurementDTOValidator, SensorService sensorService) {
        this.measurementService = measurementService;
        this.modelMapper = modelMapper;
        this.measurementDTOValidator = measurementDTOValidator;
        this.sensorService = sensorService;
    }


    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addMeasurement(@RequestBody @Valid MeasurementDTO measurementDTO,
                                                     BindingResult bindingResult) {
        measurementDTOValidator.validate(measurementDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();

            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMessage.append(fieldError.getField())
                        .append("---")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            }
            throw new MeasurementNotAddedException(errorMessage.toString());
        }
        measurementService.save(convertToMeasurement(measurementDTO));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        Measurement measurement = new Measurement();
        measurement.setValue(measurementDTO.getValue());
        measurement.setRaining(measurementDTO.getRaining());
        measurement.setSensor(sensorService.getSensorByName(measurementDTO.getSensor()));

        return measurement;
    }

    @GetMapping
    public ResponseEntity<List<MeasurementDTO>> getMeasurements() {
        List<MeasurementDTO> measurementDTOS = measurementService
                .findAll()
                .stream()
                .map(this::convertToMeasurementDTO)
                .toList();
        return new ResponseEntity<>(measurementDTOS, HttpStatus.OK);
    }

    @GetMapping("/rainyDaysCount")
    public ResponseEntity<Long> getRainyDaysCount() {
        return new ResponseEntity<>(measurementService.countByRainingTrue(), HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<MeasurementErrorResponse> handleException(MeasurementNotAddedException ex) {
        MeasurementErrorResponse response = new MeasurementErrorResponse(ex.getMessage(), LocalDate.now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }
}
