package com.marathonrideshare.rideshare.controllers;

import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.services.RideBookingService;
import com.marathonrideshare.rideshare.services.RideCreationService;
import com.marathonrideshare.rideshare.services.RideQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
public class RideShareController {

    private final RideCreationService rideCreationService;
    private final RideBookingService rideBookingService;
    private final RideQueryService rideQueryService;

    @Autowired
    public RideShareController(RideCreationService rideCreationService,
                               RideBookingService rideBookingService,
                               RideQueryService rideQueryService) {
        this.rideCreationService = rideCreationService;
        this.rideBookingService = rideBookingService;
        this.rideQueryService = rideQueryService;
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
    public ResponseEntity<RideDetailResponse> getRideDetails(@PathVariable String rideId) {
        RideDetailResponse response = rideQueryService.getRideDetails(rideId);
        return ResponseEntity.ok(response);
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
