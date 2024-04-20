package com.marathonrideshare.rideshare.dto;

import com.marathonrideshare.rideshare.shared.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SearchRideRequest {
    private Location location;
    private LocalDateTime datetime;
}
