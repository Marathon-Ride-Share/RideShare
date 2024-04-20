package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.components.UserServiceClient;
import com.marathonrideshare.rideshare.dto.CreateRideRequest;
import com.marathonrideshare.rideshare.dto.KafkaChatGroupEvent;
import com.marathonrideshare.rideshare.dto.RideInfo;
import com.marathonrideshare.rideshare.dto.UserInfo;
import com.marathonrideshare.rideshare.models.Location;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RideService {

    private static final String AVAILABLE = "AVAILABLE";
    private static final String USER_CHAT_TOPIC = "user-chat";

    private final UserServiceClient userServiceClient;
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, KafkaChatGroupEvent> kafkaTemplate;

    @Autowired
    public RideService(UserServiceClient userServiceClient, RideRepository rideRepository,
                       KafkaTemplate<String, KafkaChatGroupEvent> kafkaTemplate) {
        this.userServiceClient = userServiceClient;
        this.rideRepository = rideRepository;
        this.kafkaTemplate = kafkaTemplate;
    }
    public RideInfo createRide(CreateRideRequest request) {
        UserInfo userInfo = userServiceClient.getUserInfo(request.getUserName());

        Location origin = Location.builder()
                .latitude(request.getOrigin().getLatitude())
                .longitude(request.getOrigin().getLongitude())
                .build();

        Location destination = Location.builder()
                .latitude(request.getDestination().getLatitude())
                .longitude(request.getDestination().getLongitude())
                .build();


        // build ride model
        Ride ride = Ride.builder()
                .driverName(userInfo.getDriverInfo().getDriverName())
                .origin(origin)
                .destination(destination)
                .price(request.getPrice())
                .startTime(request.getStartTime())
                .status(AVAILABLE)
                .availableSeats(request.getAvailableSeats())
                .build();

        // Save ride to rides collection
        Ride savedRide = rideRepository.save(ride);

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

        return RideInfo.builder()
                .rideId(savedRide.getRideId())
                .availableSeats(savedRide.getAvailableSeats())
                .passengers(savedRide.getPassengers())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .price(request.getPrice())
                .startTime(request.getStartTime())
                .driverInfo(userInfo.getDriverInfo())
                .vehicle(userInfo.getVehicle())
                .status(AVAILABLE)
                .build();
    }
}
