package com.marathonrideshare.rideshare.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.models.RideAssociation;
import com.marathonrideshare.rideshare.models.UserRides;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.repositories.UserRidesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideCreationService {

    private static final Logger logger = LoggerFactory.getLogger(RideCreationService.class);
    private static final String CREATED = "CREATED";
    private static final boolean TRUE = true;
    private static final String USER_CHAT = "user-chat";

    private final UserServiceClient userServiceClient;
    private final RideRepository rideRepository;
    private final UserRidesRepository userRidesRepository;
    private final KafkaTemplate<String, String> kafkaChatGroupEventTemplate;

    @Autowired
    public RideCreationService(UserServiceClient userServiceClient, RideRepository rideRepository,
            UserRidesRepository userRidesRepository,
            KafkaTemplate<String, String> kafkaChatGroupEventTemplate) {
        this.userServiceClient = userServiceClient;
        this.rideRepository = rideRepository;
        this.userRidesRepository = userRidesRepository;
        this.kafkaChatGroupEventTemplate = kafkaChatGroupEventTemplate;
    }

    public CreateRideResponse createRide(CreateRideRequest request) throws RideShareException {
        try {
            UserInfo userInfo = userServiceClient.getUserInfo(request.getUserName());

            System.out.println("userInfo!!!"+userInfo);

            // build ride model
            Ride ride = Ride.builder()
                    .driverName(userInfo.getDriverInfo().getDriverName())
                    .origin(request.getOrigin())
                    .destination(request.getDestination())
                    .price(request.getPrice())
                    .startTime(request.getStartTime())
                    .passengers(List.of())
                    .status(CREATED)
                    .availableSeats(request.getAvailableSeats())
                    .build();

            // Save ride to rides collection
            Ride savedRide = rideRepository.save(ride);

            // Create a RideAssociation object
            RideAssociation rideAssociation = RideAssociation.builder()
                    .rideId(savedRide.getRideId())
                    .isDriver(TRUE)
                    .build();

            // Add the rideAssociation to user's rides in userRides collection
            UserRides userRides = userRidesRepository.findUserRidesByUserName(request.getUserName());

            if (userRides != null) {
                userRides.getRides().add(rideAssociation);
            } else {
                userRides = UserRides.builder()
                        .userName(request.getUserName())
                        .rides(List.of(rideAssociation))
                        .build();
            }

            userRidesRepository.save(userRides);

            // create Kafka event
            KafkaChatGroupEvent kafkaChatGroupEvent = KafkaChatGroupEvent.builder()
                    .rideId(savedRide.getRideId())
                    .userName(userInfo.getDriverInfo().getDriverName())
                    .origin(request.getOrigin())
                    .destination(request.getDestination())
                    .action(KafkaChatGroupEvent.Action.CREATE)
                    .build();

            String kafkaChatGroupEventJson = new ObjectMapper().writeValueAsString(kafkaChatGroupEvent);

            // Send Kafka event
            kafkaChatGroupEventTemplate.send(USER_CHAT, kafkaChatGroupEventJson);

            return CreateRideResponse.builder().ride(
                    RideInfo.builder()
                            .rideId(savedRide.getRideId())
                            .availableSeats(savedRide.getAvailableSeats())
                            .passengers(savedRide.getPassengers())
                            .origin(request.getOrigin())
                            .destination(request.getDestination())
                            .price(request.getPrice())
                            .startTime(request.getStartTime())
                            .driverInfo(userInfo.getDriverInfo())
                            .vehicleInfo(userInfo.getVehicleInfo())
                            .status(CREATED)

                            .build())
                    .build();
        } catch (RideShareException e) {
         logger.error("Error creating ride: {}", e.getMessage());
         throw new RideShareException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating ride: {}", e.getMessage());
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create ride");
        }
    }

}
