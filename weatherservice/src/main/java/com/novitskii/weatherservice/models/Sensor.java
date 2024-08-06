package com.novitskii.weatherservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;


    @Column(name = "name")
    @NotEmpty
    @Size(min = 3, max = 30, message = "Sensor name should be between 3 and 30")
    private String name;


    @Column(name = "registered_at")
    LocalDateTime registeredAt;

    @OneToMany(mappedBy = "sensor", fetch = FetchType.EAGER)
    @Cascade(CascadeType.REMOVE)
    private List<Measurement> measurementList = new ArrayList<>();

}
