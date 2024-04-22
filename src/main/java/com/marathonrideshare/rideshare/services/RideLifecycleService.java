package com.marathonrideshare.rideshare.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.marathonrideshare.rideshare.dto.CompleteRideResponse;
import com.marathonrideshare.rideshare.dto.KafkaChatGroupEvent;
import com.marathonrideshare.rideshare.dto.KafkaPaymentEvent;
import com.marathonrideshare.rideshare.dto.StartRideResponse;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RideLifecycleService {
    private static final Logger logger = LoggerFactory.getLogger(RideLifecycleService.class);
    private static final String USER_CHAT = "user-chat";
    private static final String USER_PAYMENT = "user-payment";
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, String> kafkaPaymentEventTemplate;
    private final KafkaTemplate<String, String> kafkaChatGroupEventTemplate;

    @Autowired
    public RideLifecycleService(RideRepository rideRepository,
                                KafkaTemplate<String, String> kafkaPaymentEventTemplate,
                                KafkaTemplate<String, String> kafkaChatGroupEventTemplate) {
        this.rideRepository = rideRepository;
        this.kafkaPaymentEventTemplate = kafkaPaymentEventTemplate;
        this.kafkaChatGroupEventTemplate = kafkaChatGroupEventTemplate;
    }

    public StartRideResponse startRide(String rideId, LocalDateTime startTime) throws RideShareException {
        try {
            Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
            ride.setStatus("IN_PROGRESS");
            ride.setStartTime(startTime);
            rideRepository.save(ride);

            return StartRideResponse.builder()
                    .rideId(rideId)
                    .status("IN_PROGRESS")
                    .startTime(startTime)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to start ride", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to start ride");
        }
    }

    public CompleteRideResponse completeRide(String rideId, LocalDateTime endTime) throws RideShareException {
        try {
            Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
            ride.setStatus("COMPLETED");
            ride.setEndTime(endTime);
            rideRepository.save(ride);

            // create Kafka event
            KafkaChatGroupEvent kafkaChatGroupEvent = KafkaChatGroupEvent.builder()
                    .rideId(ride.getRideId())
                    .userName(ride.getDriverName())
                    .origin(ride.getOrigin())
                    .destination(ride.getDestination())
                    .action(KafkaChatGroupEvent.Action.DELETE)
                    .build();

            String kafkaChatGroupEventJson = new ObjectMapper().writeValueAsString(kafkaChatGroupEvent);

            // Send Kafka event
            kafkaChatGroupEventTemplate.send(USER_CHAT, kafkaChatGroupEventJson);

            // create kafka payment event for all passengers
            ride.getPassengers().forEach(passenger -> {
                KafkaPaymentEvent kafkaPaymentEvent = KafkaPaymentEvent.builder()
                        .paymentOrderId(passenger.getPaymentOrderId())
                        .passengerName(passenger.getPassengerName())
                        .driverName(ride.getDriverName())
                        .rideId(ride.getRideId())
                        .build();
                try {
                    String kafkaPaymentEventJson = new ObjectMapper().writeValueAsString(kafkaPaymentEvent);
                    kafkaPaymentEventTemplate.send(USER_PAYMENT, kafkaPaymentEventJson);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

            return CompleteRideResponse.builder()
                    .rideId(rideId)
                    .status("COMPLETED")
                    .arrivalTime(endTime)
                    .build();
        } catch (Exception e) {
            logger.error("Failed to complete ride", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to complete ride");
        }
    }
}
