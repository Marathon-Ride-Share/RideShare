package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookRideRequest {
    private String userName;
    private String rideId;
    private Location pickupLocation;
}
