package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
public class CompleteRideResponse {
    private String rideId;
    private String status;
    private LocalDateTime arrivalTime;
}
