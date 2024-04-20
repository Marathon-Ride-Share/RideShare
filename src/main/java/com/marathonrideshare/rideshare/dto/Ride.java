package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class Ride {
    private String rideId;
    private Location origin;
    private Location destination;
    private DriverInfo driverInfo;
    private Vehicle vehicle;
    private float price;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private int availableSeats;
    private List<String> passengers;
}
