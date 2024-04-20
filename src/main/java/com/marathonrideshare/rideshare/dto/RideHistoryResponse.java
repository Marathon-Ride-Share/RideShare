package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RideHistoryResponse {
    private List<String> driverRideIds;
    private List<String> passengerRideIds;
}
