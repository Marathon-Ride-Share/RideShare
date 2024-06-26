package com.marathonrideshare.rideshare.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.marathonrideshare.rideshare.components.MapBoxServiceClient;
import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.models.RideAssociation;
import com.marathonrideshare.rideshare.models.UserRides;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.repositories.UserRidesRepository;
import com.marathonrideshare.rideshare.shared.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideQueryService {

    private static final Logger logger = LoggerFactory.getLogger(RideQueryService.class);
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
    public RideDetailResponse getRideDetails(String rideId) throws RideShareException {
        try {
            // get ride details
            Ride ride = rideRepository.findById(rideId)
                    .orElseThrow(() -> new RideShareException(HttpStatus.NOT_FOUND.value(), "Ride not found with id: " + rideId));
            UserInfo userInfo = userServiceClient.getUserInfo(ride.getDriverName());

            // return ride details
            RideInfo rideInfo = RideInfo.builder()
                    .rideId(ride.getRideId())
                    .driverInfo(userInfo.getDriverInfo())
                    .vehicleInfo(userInfo.getVehicleInfo())
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
        } catch (RideShareException e) {
            logger.error("Failed to get ride details", e);
            throw new RideShareException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to get ride details", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to get ride details");
        }
    }

    public SearchRideResponse searchRides(SearchRideRequest request) throws RideShareException {

        System.out.println("Request for search Ride: " + request);

        try {
            // get all rides whose status is CREATED and not yet STARTED
            List<Ride> allRides = rideRepository.findByStatus(CREATED);

            System.out.println("List of all rides in db: " + allRides);

            // filter rides by origin and destination within 50 km and available seats and date time
            List<Ride> validRides = allRides.stream()
                    .filter(ride -> isWithin50Km(ride.getOrigin(), request.getLocation()) || isWithin50Km(ride.getDestination(), request.getLocation()))
                    .filter(ride -> ride.getAvailableSeats() > 0)
                    .filter(ride -> ride.getStartTime().toLocalDate().isEqual(request.getDatetime().toLocalDate()))
                    .filter(ride -> ride.getStartTime().isBefore(request.getDatetime()))
                    .toList();

            System.out.println("List of all valid rides in db: " + validRides);

            // map rides to RideInfo
            List<RideInfo> ridesInfos = validRides.stream()
                    .map(ride -> {
                        try {
                            UserInfo userInfo = userServiceClient.getUserInfo(ride.getDriverName());
                            return RideInfo.builder()
                                    .rideId(ride.getRideId())
                                    .driverInfo(userInfo.getDriverInfo())
                                    .vehicleInfo(userInfo.getVehicleInfo())
                                    .origin(ride.getOrigin())
                                    .destination(ride.getDestination())
                                    .price(ride.getPrice())
                                    .startTime(ride.getStartTime())
                                    .endTime(ride.getEndTime())
                                    .status(ride.getStatus())
                                    .availableSeats(ride.getAvailableSeats())
                                    .passengers(ride.getPassengers())
                                    .build();
                        } catch (RideShareException e) {
                            logger.error("Failed to get user info for ride: {}", ride.getRideId(), e);
                            throw new RuntimeException("Failed to get user info for ride: " + ride.getRideId());
                        }
                    })
                    .toList();

            // get rides by origin and destination
            return SearchRideResponse.builder()
                    .rides(ridesInfos)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to search rides", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to get rides near you");
        }
    }

    public RideHistoryResponse getRideHistory(String userId) throws RideShareException {
        try {
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
        } catch (Exception e) {
            logger.error("Failed to get ride history for user: {}", userId, e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to get ride history for user: " + userId);
        }
    }

    private boolean isWithin50Km(Location location1, Location location2) {
        try {
            double distance = mapBoxServiceClient.getDistance(location1.getLongitude(), location1.getLatitude(),
                    location2.getLongitude(), location2.getLatitude());
            return distance <= 50;
        } catch (Exception e) {
            return false;
        }
    }
}
