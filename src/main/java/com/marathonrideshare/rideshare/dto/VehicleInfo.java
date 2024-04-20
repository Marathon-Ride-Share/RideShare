package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VehicleInfo {
    private String make;
    private String model;
    private String color;
    private String licensePlate;
}
