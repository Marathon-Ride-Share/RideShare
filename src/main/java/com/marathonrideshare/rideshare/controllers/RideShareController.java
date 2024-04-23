package com.marathonrideshare.rideshare.controllers;

import com.marathonrideshare.rideshare.dto.*;
import com.marathonrideshare.rideshare.exceptions.RideShareException;
import com.marathonrideshare.rideshare.services.RideBookingService;
import com.marathonrideshare.rideshare.services.RideCreationService;
import com.marathonrideshare.rideshare.services.RideLifecycleService;
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
    private final RideLifecycleService rideLifecycleService;

    @Autowired
    public RideShareController(RideCreationService rideCreationService,
                               RideBookingService rideBookingService,
                               RideQueryService rideQueryService,
                               RideLifecycleService rideLifecycleService) {
        this.rideCreationService = rideCreationService;
        this.rideBookingService = rideBookingService;
        this.rideQueryService = rideQueryService;
        this.rideLifecycleService = rideLifecycleService;
    }
    @PostMapping("/rides")
    public ResponseEntity<?> createRide(@RequestBody CreateRideRequest request) {
        try {
            CreateRideResponse response = rideCreationService.createRide(request);
            return ResponseEntity.ok(response);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @PostMapping("/rides/book")
    public ResponseEntity<?> bookRide(@RequestBody BookRideRequest request) {
        try {
            BookRideResponse response = rideBookingService.bookRide(request);
            return ResponseEntity.ok(response);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/rides/{rideId}")
    public ResponseEntity<?> getRideDetails(@PathVariable String rideId) {
        try {
            RideDetailResponse response = rideQueryService.getRideDetails(rideId);
            return ResponseEntity.ok(response);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @PostMapping("/rides/search")
    public ResponseEntity<?> searchRides(@RequestBody SearchRideRequest request) {
        try {
            SearchRideResponse response = rideQueryService.searchRides(request);
            return ResponseEntity.ok(response);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @PatchMapping("/rides/{rideId}/start")
    public ResponseEntity<?> startRide(@PathVariable String rideId, @RequestBody StartRideRequest request) {
        try {
            StartRideResponse startRideResponse = rideLifecycleService.startRide(rideId, request.getStartTime());
            return ResponseEntity.ok(startRideResponse);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @PatchMapping("/rides/{rideId}/complete")
    public ResponseEntity<?> completeRide(@PathVariable String rideId, @RequestBody CompleteRideRequest request) {
        try {
            CompleteRideResponse completeRideResponse = rideLifecycleService.completeRide(rideId, request.getCompletionTime());
            return ResponseEntity.ok(completeRideResponse);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @GetMapping("/rides/history/{userId}")
    public ResponseEntity<?> getRideHistory(@PathVariable String userId) {
        try {
            RideHistoryResponse response = rideQueryService.getRideHistory(userId);
            return ResponseEntity.ok(response);
        } catch (RideShareException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

}
