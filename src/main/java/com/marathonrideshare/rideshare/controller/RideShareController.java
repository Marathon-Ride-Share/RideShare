package com.marathonrideshare.rideshare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class RideShareController {
    @PostMapping("/rides")
    public void createRide() {
        // Create a new ride
    }

    @PostMapping("/rides/book/")
    public void bookRide() {
        // Book a ride
    }

    @GetMapping("/rides/{rideId}")
    public void getRideDetails() {
        // Get ride details
    }

    @PostMapping("/rides/search")
    public void searchRides() {
        // Search for rides
    }

    @PatchMapping("/rides/{rideId}/start")
    public void startRide() {
        // Start a ride
    }

    @PatchMapping("/rides/{rideId}/complete")
    public void completeRide() {
        // Complete a ride
    }

    @GetMapping("/rides/history/{userId}")
    public void getRideHistory() {
        // Get ride history
    }

}
