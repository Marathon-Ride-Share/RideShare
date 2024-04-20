package com.marathonrideshare.rideshare.controllers;

import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.services.RideBookingService;
import com.marathonrideshare.rideshare.services.RideCreationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
public class RideShareController {

    private final RideCreationService rideCreationService;
    private final RideBookingService rideBookingService;

    @Autowired
    public RideShareController(RideCreationService rideCreationService,
                               RideBookingService rideBookingService) {
        this.rideCreationService = rideCreationService;
        this.rideBookingService = rideBookingService;
    }
    @PostMapping("/rides")
    public ResponseEntity<CreateRideResponse> createRide(@RequestBody CreateRideRequest request) {
        CreateRideResponse response = rideCreationService.createRide(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rides/book")
    public ResponseEntity<BookRideResponse> bookRide(@RequestBody BookRideRequest request) {
        BookRideResponse response = rideBookingService.bookRide(request);
        return ResponseEntity.ok(response);
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
