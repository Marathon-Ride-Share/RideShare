package com.marathonrideshare.rideshare.components;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.HashMap;

@Component
public class PaymentServiceClient {
    private final RestTemplate restTemplate;

    @Value("${paymentService.url}")
    private String paymentServiceUrl;

    @Autowired
    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String createPaymentOrder(String userId, double amount) {
        String url = paymentServiceUrl;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() && response.getBody() == null) {
            throw new RuntimeException("Failed to create payment order");
        }

        JSONObject json = new JSONObject(response.getBody());
        boolean isOrderSuccessful = json.getBoolean("isOrderSuccessful");
        String paymentOrderId = json.getString("paymentOrderId");

        if(!isOrderSuccessful) {
            throw new RuntimeException("Failed to create payment order");
        }

        return paymentOrderId;
    }
}
