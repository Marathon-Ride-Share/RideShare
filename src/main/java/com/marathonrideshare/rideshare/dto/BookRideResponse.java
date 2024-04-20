package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookRideResponse {
    private String bookingId;
    private String rideId;
    private String driverName;
    private String userName;
    private String status;
    private LocationInfo pickupLocation;
}
