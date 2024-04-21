package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.RideDetailResponse;
import com.marathonrideshare.rideshare.dto.RideInfo;
import com.marathonrideshare.rideshare.dto.UserInfo;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RideQueryService {
    private final RideRepository rideRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public RideQueryService(RideRepository rideRepository,
                            UserServiceClient userServiceClient) {
        this.rideRepository = rideRepository;
        this.userServiceClient = userServiceClient;
    }
    public RideDetailResponse getRideDetails(String rideId) {
        // get ride details
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));

        UserInfo userInfo = userServiceClient.getUserInfo(ride.getDriverName());

        // return ride details
        RideInfo rideInfo = RideInfo.builder()
                .rideId(ride.getRideId())
                .driverInfo(userInfo.getDriverInfo())
                .vehicleInfo(userInfo.getVehicle())
                .origin(ride.getOrigin())
                .destination(ride.getDestination())
                .price(ride.getPrice())
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .status(ride.getStatus())
                .availableSeats(ride.getAvailableSeats())
                .passengers(ride.getPassengers())
                .build();

        return RideDetailResponse.builder()
                .ride(rideInfo)
                .build();
    }
}
