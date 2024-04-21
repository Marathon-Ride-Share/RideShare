package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.dto.CompleteRideResponse;
import com.marathonrideshare.rideshare.dto.StartRideResponse;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RideLifecycleService {
    private final RideRepository rideRepository;

    @Autowired
    public RideLifecycleService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public StartRideResponse startRide(String rideId, LocalDateTime startTime) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus("IN_PROGRESS");
        ride.setStartTime(startTime);
        rideRepository.save(ride);

        return StartRideResponse.builder()
                .rideId(rideId)
                .status("IN_PROGRESS")
                .startTime(startTime)
                .build();
    }

    public CompleteRideResponse completeRide(String rideId, LocalDateTime endTime) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus("COMPLETED");
        ride.setEndTime(endTime);
        rideRepository.save(ride);

        return CompleteRideResponse.builder()
                .rideId(rideId)
                .status("COMPLETED")
                .arrivalTime(endTime)
                .build();
    }
}
