package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RideHistoryResponse {
    private List<String> driverRideIds;
    private List<String> passengerRideIds;
}
