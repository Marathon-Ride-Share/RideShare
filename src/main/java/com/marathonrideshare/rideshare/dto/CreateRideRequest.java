package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreateRideRequest {
    private String userName;
    private Location origin;
    private Location destination;
    private LocalDateTime startTime;
    private float price;
    private int availableSeats;
}
