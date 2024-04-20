package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vehicle {
    private String make;
    private String model;
    private String color;
    private String licensePlate;
}
