package com.marathonrideshare.rideshare.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class RideAssociation {
    @NonNull
    private String rideId;

    private boolean isDriver;
}
