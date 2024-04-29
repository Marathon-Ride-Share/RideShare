package com.marathonrideshare.rideshare.components;

import com.marathonrideshare.rideshare.exceptions.RideShareException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@Component
public class PaymentServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);
    private final RestTemplate restTemplate;

    @Value("${paymentService.url}/new")
    private String paymentServiceUrl;

    @Autowired
    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createPaymentOrder(String userId, double amount) throws RideShareException {
        try {
            String url = paymentServiceUrl;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // create payment order
            Map<String, Object> map = new HashMap<>();
            map.put("userId", userId);
            map.put("amount", amount);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

            // send request to payment service
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // parse response
            if (!response.getStatusCode().is2xxSuccessful() && response.getBody() == null) {
                throw new RuntimeException("Failed to create payment order");
            }

            JSONObject json = new JSONObject(response.getBody());
            boolean isOrderSuccessful = json.getBoolean("success");
            String paymentOrderId = json.getString("order_id");

            if (!isOrderSuccessful) {
                throw new RuntimeException("Failed to create payment order");
            }

            return paymentOrderId;
        } catch (Exception e) {
            logger.error("Failed to create payment order", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create payment order");
        }
    }
}
