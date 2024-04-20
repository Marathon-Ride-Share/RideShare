package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RideInfo {
    private String rideId;
    private LocationInfo origin;
    private LocationInfo destination;
    private DriverInfo driverInfo;
    private VehicleInfo vehicle;
    private float price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private int availableSeats;
    private List<String> passengers;
}
