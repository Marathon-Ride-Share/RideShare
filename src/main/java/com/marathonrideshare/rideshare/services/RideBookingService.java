package com.marathonrideshare.rideshare.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.marathonrideshare.rideshare.dto.BookRideRequest;
import com.marathonrideshare.rideshare.dto.BookRideResponse;
import com.marathonrideshare.rideshare.dto.KafkaChatGroupEvent;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.models.RideAssociation;
import com.marathonrideshare.rideshare.models.UserRides;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import com.marathonrideshare.rideshare.repositories.UserRidesRepository;
import com.marathonrideshare.rideshare.shared.Passenger;
import com.marathonrideshare.rideshare.components.PaymentServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideBookingService {

    private static final Logger logger = LoggerFactory.getLogger(RideBookingService.class);
    private static final String USER_CHAT = "user-chat";
    private static final boolean FALSE = false;

    private final PaymentServiceClient paymentServiceClient;
    private final RideRepository rideRepository;
    private final UserRidesRepository userRidesRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public RideBookingService(PaymentServiceClient paymentServiceClient,
                              RideRepository rideRepository,
                              UserRidesRepository userRidesRepository,
                              KafkaTemplate<String, String> kafkaTemplate) {
        this.paymentServiceClient = paymentServiceClient;
        this.rideRepository = rideRepository;
        this.userRidesRepository = userRidesRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BookRideResponse bookRide(BookRideRequest request) throws RideShareException {
        try {
            // get ride by id
            Ride ride = rideRepository.findById(request.getRideId()).orElseThrow(() -> new RuntimeException("Ride not found"));

            // create a payment order for the user
            String paymentOrderId = paymentServiceClient.createPaymentOrder(request.getUserName(), ride.getPrice());

            // add current user to passengers for the ride.
            ride.getPassengers().add(
                    Passenger.builder()
                            .passengerName(request.getUserName())
                            .pickUpLocation(request.getPickupLocation())
                            .paymentOrderId(paymentOrderId)
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

            String kafkaChatGroupEventJson = new ObjectMapper().writeValueAsString(kafkaChatGroupEvent);

            // Send Kafka event
            kafkaTemplate.send(USER_CHAT, kafkaChatGroupEventJson);

            return BookRideResponse.builder()
                    .rideId(request.getRideId())
                    .driverName(ride.getDriverName())
                    .userName(request.getUserName())
                    .status(ride.getStatus())
                    .pickupLocation(request.getPickupLocation())
                    .build();
        } catch (RideShareException e) {
            logger.error("Failed to book ride", e);
            throw new RideShareException(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to book ride", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to book ride");
        }
    }
}
