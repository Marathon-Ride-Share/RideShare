package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SearchRideResponse {
    private List<RideInfo> rides;
}
