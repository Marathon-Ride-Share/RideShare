package com.marathonrideshare.rideshare.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MapBoxServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(MapBoxServiceClient.class);
    private final RestTemplate restTemplate;

    @Value("${mapbox.directions.url}")
    private String mapboxDirectionsUrl;

    @Value("${mapbox.access.token}")
    private String mapboxAccessToken;

    @Autowired
    public MapBoxServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double getDistance(double lon1, double lat1, double lon2, double lat2) throws RideShareException {
        try {
            String coordinates = String.format("%f,%f;%f,%f", lon1, lat1, lon2, lat2);
            String url = mapboxDirectionsUrl + "/" + coordinates + "?access_token=" + mapboxAccessToken;

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JSONObject json = new JSONObject(response.getBody());
                if (!json.getJSONArray("routes").isEmpty()) {
                    double distance = json.getJSONArray("routes").getJSONObject(0).getDouble("distance");
                    return distance / 1000;
                }
            }
            return 0;
        } catch (Exception e) {
            logger.error("Failed to get distance", e);
            throw new RideShareException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to get distance");
        }
    }
}
