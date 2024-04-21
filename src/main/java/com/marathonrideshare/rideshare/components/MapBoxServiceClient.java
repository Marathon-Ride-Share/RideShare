package com.marathonrideshare.rideshare.components;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MapBoxServiceClient {
    private final RestTemplate restTemplate;

    @Value("${mapbox.directions.url}")
    private String mapboxDirectionsUrl;

    @Value("${mapbox.access.token}")
    private String mapboxAccessToken;

    @Autowired
    public MapBoxServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getDistance(double lon1, double lat1, double lon2, double lat2) {
        String coordinates = String.format("%f,%f;%f,%f", lon1, lat1, lon2, lat2);
        String url = mapboxDirectionsUrl + "/" + coordinates + "?access_token=" + mapboxAccessToken;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JSONObject json = new JSONObject(response.getBody());
            if (!json.getJSONArray("routes").isEmpty()) {
                double distance =  json.getJSONArray("routes").getJSONObject(0).getDouble("distance");
                return distance / 1000;
            }
        }
        return 0;
    }
}
