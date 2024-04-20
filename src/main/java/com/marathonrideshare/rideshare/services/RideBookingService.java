package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.dto.BookRideRequest;
import com.marathonrideshare.rideshare.dto.BookRideResponse;
import com.marathonrideshare.rideshare.dto.KafkaChatGroupEvent;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.models.RideAssociation;
import com.marathonrideshare.rideshare.models.UserRides;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.repositories.UserRidesRepository;
import com.marathonrideshare.rideshare.shared.Passenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideBookingService {
    private static final String USER_CHAT_TOPIC = "user-chat";
    private static final boolean FALSE = false;

    private final RideRepository rideRepository;
    private final UserRidesRepository userRidesRepository;
    private final KafkaTemplate<String, KafkaChatGroupEvent> kafkaTemplate;

    @Autowired
    public RideBookingService(RideRepository rideRepository,
                              UserRidesRepository userRidesRepository,
                              KafkaTemplate<String, KafkaChatGroupEvent> kafkaTemplate) {
        this.rideRepository = rideRepository;
        this.userRidesRepository = userRidesRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BookRideResponse bookRide(BookRideRequest request) {
        // get ride by id
        Ride ride = rideRepository.findById(request.getRideId()).orElseThrow(() -> new RuntimeException("Ride not found"));

        // add current user to passengers for the ride
        ride.getPassengers().add(
                Passenger.builder()
                        .passengerName(request.getUserName())
                        .pickUpLocation(request.getPickupLocation())
                        .build()
        );

        // save ride
        rideRepository.save(ride);

        // Create a RideAssociation object
        RideAssociation rideAssociation = RideAssociation.builder()
                .rideId(ride.getRideId())
                .isDriver(FALSE)
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
                .rideId(request.getRideId())
                .userName(request.getUserName())
                .action(KafkaChatGroupEvent.Action.ADD_USER)
                .build();

        // Send Kafka event
        kafkaTemplate.send(USER_CHAT_TOPIC, kafkaChatGroupEvent);

        return BookRideResponse.builder()
                .rideId(request.getRideId())
                .driverName(ride.getDriverName())
                .userName(request.getUserName())
                .status(ride.getStatus())
                .pickupLocation(request.getPickupLocation())
                .build();
    }
}
