package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
public class CompleteRideResponse {
    private String rideId;
    private String status;
    private LocalDateTime arrivalTime;
    private float fare;
}
