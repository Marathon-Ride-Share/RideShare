package com.marathonrideshare.rideshare.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.marathonrideshare.rideshare.dto.UserInfo;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
    private final RestTemplate restTemplate;

    @Value("${userService.url}")
    private String userServiceUrl;

    @Autowired
    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserInfo getUserInfo(String userId) throws RideShareException {
        try {
            // get user info
            String url = userServiceUrl + userId;
            System.out.println("match USer class"+restTemplate.getForObject(url, UserInfo.class));
            return restTemplate.getForObject(url, UserInfo.class);
        } catch (Exception e) {
            logger.error("Failed to get user info", e);
            throw new RideShareException(HttpStatus.BAD_GATEWAY.value(), "Failed to get user info");
        }
    }

}
