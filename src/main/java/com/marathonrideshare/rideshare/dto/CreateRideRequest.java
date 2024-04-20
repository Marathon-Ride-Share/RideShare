package com.marathonrideshare.rideshare.dto;

import com.marathonrideshare.rideshare.shared.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CreateRideRequest {
    private String userName;
    private Location origin;
    private Location destination;
    private LocalDateTime startTime;
    private float price;
    private int availableSeats;
}
