package com.marathonrideshare.rideshare.controllers;

import com.marathonrideshare.rideshare.dto.CreateRideRequest;
import com.marathonrideshare.rideshare.dto.CreateRideResponse;
import com.marathonrideshare.rideshare.dto.RideInfo;
import com.marathonrideshare.rideshare.services.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
public class RideShareController {

    private final RideService rideService;

    @Autowired
    public RideShareController(RideService rideService) {
        this.rideService = rideService;
    }
    @PostMapping("/rides")
    public ResponseEntity<CreateRideResponse> createRide(@RequestBody CreateRideRequest request) {
        RideInfo ride = rideService.createRide(request);
        return ResponseEntity.ok(new CreateRideResponse(ride));
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
