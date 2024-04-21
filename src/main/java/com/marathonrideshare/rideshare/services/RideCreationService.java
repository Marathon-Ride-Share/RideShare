package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.models.RideAssociation;
import com.marathonrideshare.rideshare.models.UserRides;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.repositories.UserRidesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideCreationService {

    private static final String AVAILABLE = "AVAILABLE";
    private static final boolean TRUE = true;
    private static final String USER_CHAT_TOPIC = "user-chat";

    private final UserServiceClient userServiceClient;
    private final RideRepository rideRepository;
    private final UserRidesRepository userRidesRepository;
    private final KafkaTemplate<String, KafkaChatGroupEvent> kafkaTemplate;

    @Autowired
    public RideCreationService(UserServiceClient userServiceClient, RideRepository rideRepository,
                               UserRidesRepository userRidesRepository,
                               KafkaTemplate<String, KafkaChatGroupEvent> kafkaTemplate) {
        this.userServiceClient = userServiceClient;
        this.rideRepository = rideRepository;
        this.userRidesRepository = userRidesRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    public CreateRideResponse createRide(CreateRideRequest request) {
        UserInfo userInfo = userServiceClient.getUserInfo(request.getUserName());


        // build ride model
        Ride ride = Ride.builder()
                .driverName(userInfo.getDriverInfo().getDriverName())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .price(request.getPrice())
                .startTime(request.getStartTime())
                .status(AVAILABLE)
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

        // Send Kafka event
        kafkaTemplate.send(USER_CHAT_TOPIC, kafkaChatGroupEvent);

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
                        .vehicleInfo(userInfo.getVehicle())
                        .status(AVAILABLE)
                        .build()
        ).build();
    }


}
