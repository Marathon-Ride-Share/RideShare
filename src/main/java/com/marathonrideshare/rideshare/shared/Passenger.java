package com.marathonrideshare.rideshare.shared;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
public class Passenger {
    @NonNull
    private String passengerName;
    @NonNull
    private Location pickUpLocation;
    private String paymentOrderId;
}
