package com.novitskii.weatherservice.utils.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SensorErrorResponse {
    private String errorMessage;
    private LocalDate timestamp;
}
