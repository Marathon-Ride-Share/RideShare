package com.marathonrideshare.rideshare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class KafkaPaymentEvent {

    private String paymentOrderId;
    private String rideId;
    private String passengerName;
    private String driverName;
}
