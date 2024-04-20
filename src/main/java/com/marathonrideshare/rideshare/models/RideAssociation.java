package com.marathonrideshare.rideshare.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class RideAssociation {
    @NonNull
    private String rideId;

    private boolean isDriver;
}
