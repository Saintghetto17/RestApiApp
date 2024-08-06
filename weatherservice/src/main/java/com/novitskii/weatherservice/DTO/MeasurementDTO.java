package com.novitskii.weatherservice.DTO;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementDTO {

    @NotNull
    private Float value;

    @NotNull
    private Boolean raining;

    @NotNull
    private String sensor;

}
