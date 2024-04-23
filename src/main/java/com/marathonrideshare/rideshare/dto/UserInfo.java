package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfo {
    private VehicleInfo vehicleInfo;
    private DriverInfo driverInfo;
}
