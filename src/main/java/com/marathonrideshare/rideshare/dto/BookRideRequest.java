package com.marathonrideshare.rideshare.dto;

import com.marathonrideshare.rideshare.shared.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BookRideRequest {
    private String userName;
    private String rideId;
    private Location pickupLocation;
}
