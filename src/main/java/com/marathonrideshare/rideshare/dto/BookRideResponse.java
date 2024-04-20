package com.marathonrideshare.rideshare.dto;

import com.marathonrideshare.rideshare.shared.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookRideResponse {
    private String rideId;
    private String driverName;
    private String userName;
    private String status;
    private Location pickupLocation;
}
