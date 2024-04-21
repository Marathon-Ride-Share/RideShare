package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.components.MapBoxServiceClient;
import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.models.RideAssociation;
import com.marathonrideshare.rideshare.models.UserRides;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.repositories.UserRidesRepository;
import com.marathonrideshare.rideshare.shared.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideQueryService {

    private static final String CREATED = "CREATED";
    private final RideRepository rideRepository;
    private final UserRidesRepository userRidesRepository;
    private final UserServiceClient userServiceClient;
    private final MapBoxServiceClient mapBoxServiceClient;

    @Autowired
    public RideQueryService(RideRepository rideRepository,
                            UserRidesRepository userRidesRepository,
                            UserServiceClient userServiceClient,
                            MapBoxServiceClient mapBoxServiceClient) {
        this.rideRepository = rideRepository;
        this.userRidesRepository = userRidesRepository;
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

    public RideHistoryResponse getRideHistory(String userId) {
        // get all rides for the user
        UserRides rides = userRidesRepository.findUserRidesByUserName(userId);

        // Filter rides where user is the driver
        List<String> driverRideIds = rides.getRides().stream()
                .filter(RideAssociation::isDriver)
                .map(RideAssociation::getRideId)
                .collect(Collectors.toList());

        // Filter rides where user is the passenger
        List<String> passengerRideIds = rides.getRides().stream()
                .filter(rideAssociation -> !rideAssociation.isDriver())
                .map(RideAssociation::getRideId)
                .collect(Collectors.toList());

        // Create and Return RideHistoryResponse
        return RideHistoryResponse.builder()
                .driverRideIds(driverRideIds)
                .passengerRideIds(passengerRideIds)
                .build();
    }

    private boolean isWithin5Km(Location location1, Location location2) {
        double distance = mapBoxServiceClient.getDistance(location1.getLongitude(), location1.getLatitude(),
                location2.getLongitude(), location2.getLatitude());
        return distance <= 5;
    }
}
