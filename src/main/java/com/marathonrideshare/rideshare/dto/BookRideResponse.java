package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookRideResponse {
    private String bookingId;
    private String rideId;
    private String driverName;
    private String userName;
    private String status;
    private Location pickupLocation;
}
