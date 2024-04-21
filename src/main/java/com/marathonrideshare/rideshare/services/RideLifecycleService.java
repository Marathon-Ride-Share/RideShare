package com.marathonrideshare.rideshare.services;

import com.marathonrideshare.rideshare.dto.CompleteRideResponse;
import com.marathonrideshare.rideshare.dto.KafkaChatGroupEvent;
import com.marathonrideshare.rideshare.dto.KafkaPaymentEvent;
import com.marathonrideshare.rideshare.dto.StartRideResponse;
import com.marathonrideshare.rideshare.models.Ride;
import com.marathonrideshare.rideshare.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RideLifecycleService {

    private static final String USER_CHAT = "user-chat";
    private static final String USER_PAYMENT = "payment";
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, KafkaPaymentEvent> kafkaPaymentEventTemplate;
    private final KafkaTemplate<String, KafkaChatGroupEvent> kafkaChatGroupEventTemplate;

    @Autowired
    public RideLifecycleService(RideRepository rideRepository,
                                KafkaTemplate<String, KafkaPaymentEvent> kafkaPaymentEventTemplate,
                                KafkaTemplate<String, KafkaChatGroupEvent> kafkaChatGroupEventTemplate) {
        this.rideRepository = rideRepository;
        this.kafkaPaymentEventTemplate = kafkaPaymentEventTemplate;
        this.kafkaChatGroupEventTemplate = kafkaChatGroupEventTemplate;
    }

    public StartRideResponse startRide(String rideId, LocalDateTime startTime) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus("IN_PROGRESS");
        ride.setStartTime(startTime);
        rideRepository.save(ride);

        return StartRideResponse.builder()
                .rideId(rideId)
                .status("IN_PROGRESS")
                .startTime(startTime)
                .build();
    }

    public CompleteRideResponse completeRide(String rideId, LocalDateTime endTime) {
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

        // Send Kafka event
        kafkaChatGroupEventTemplate.send(USER_CHAT, kafkaChatGroupEvent);

        // create kafka payment event for all passengers
        ride.getPassengers().forEach(passenger -> {
            KafkaPaymentEvent kafkaPaymentEvent = KafkaPaymentEvent.builder()
                    .paymentOrderId(passenger.getPaymentOrderId())
                    .passengerName(passenger.getPassengerName())
                    .driverName(ride.getDriverName())
                    .rideId(ride.getRideId())
                    .build();
            kafkaPaymentEventTemplate.send(USER_PAYMENT, kafkaPaymentEvent);
        });

        return CompleteRideResponse.builder()
                .rideId(rideId)
                .status("COMPLETED")
                .arrivalTime(endTime)
                .build();
    }
}
