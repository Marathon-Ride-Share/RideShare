package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SearchRideRequest {
    private Location location;
    private LocalDateTime datetime;
}
