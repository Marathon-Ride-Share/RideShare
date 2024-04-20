package com.marathonrideshare.rideshare.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Location {
    private float latitude;
    private float longitude;
    private String locationName;
}
