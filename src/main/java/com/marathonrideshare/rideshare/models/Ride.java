package com.marathonrideshare.rideshare.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {
    @Id
    private String rideId;

    @NonNull
    private Location origin;

    @NonNull
    private Location destination;

    @NonNull
    private String driverName;

    private float price;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @NonNull
    private String status;

    private int availableSeats;

    private List<String> passengers;
}
