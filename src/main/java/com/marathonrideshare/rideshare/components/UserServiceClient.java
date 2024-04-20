package com.marathonrideshare.rideshare.components;

import com.marathonrideshare.rideshare.dto.UserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {

    private RestTemplate restTemplate;
    private String userServiceUrl;

    @Value("${userService.url}")
    public void setApiGatewayUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    @Bean
    public RestTemplate restTemplate() {
        this.restTemplate = new RestTemplate();
        return this.restTemplate;
    }

    public UserInfo getUserInfo(String userId) {
        String url = userServiceUrl + "?userId=" + userId;
        return restTemplate.getForObject(url, UserInfo.class);
    }

}
