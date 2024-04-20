package com.marathonrideshare.rideshare.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {
    private float latitude;
    private float longitude;
    private String locationName;
}
