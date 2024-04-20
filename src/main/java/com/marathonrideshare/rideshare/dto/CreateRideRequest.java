package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CreateRideRequest {
    private String userName;
    private LocationInfo origin;
    private LocationInfo destination;
    private LocalDateTime startTime;
    private float price;
    private int availableSeats;
}
