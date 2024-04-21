package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.components.MapBoxServiceClient;
import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.shared.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideQueryService {

    private static final String CREATED = "CREATED";
    private final RideRepository rideRepository;
    private final UserServiceClient userServiceClient;
    private final MapBoxServiceClient mapBoxServiceClient;

    @Autowired
    public RideQueryService(RideRepository rideRepository,
                            UserServiceClient userServiceClient,
                            MapBoxServiceClient mapBoxServiceClient) {
        this.rideRepository = rideRepository;
        this.userServiceClient = userServiceClient;
        this.mapBoxServiceClient = mapBoxServiceClient;
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

    public SearchRideResponse searchRides(SearchRideRequest request) {

        // get all rides whose status is CREATED and not yet STARTED
        List<Ride> allRides = rideRepository.findByStatus(CREATED);

        // filter rides by origin and destination
        List<Ride> validRides = allRides.stream()
                .filter(ride -> isWithin5Km(ride.getOrigin(), request.getLocation()) || isWithin5Km(ride.getDestination(), request.getLocation()))
                .filter(ride -> ride.getAvailableSeats() > 0)
                .toList();

        // map rides to RideInfo
        List<RideInfo> ridesInfos = validRides.stream()
                .map(ride -> {
                    UserInfo userInfo = userServiceClient.getUserInfo(ride.getDriverName());
                    return RideInfo.builder()
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
                })
                .toList();

        // get rides by origin and destination
        return SearchRideResponse.builder()
                .rides(ridesInfos)
                .build();
    }

    private boolean isWithin5Km(Location location1, Location location2) {
        double distance = mapBoxServiceClient.getDistance(location1.getLongitude(), location1.getLatitude(),
                location2.getLongitude(), location2.getLatitude());
        return distance <= 5;
    }
}
