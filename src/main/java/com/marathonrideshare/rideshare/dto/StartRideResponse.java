package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StartRideResponse {
    private String rideId;
    private String status;
    private LocalDateTime startTime;
}
