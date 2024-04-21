package com.marathonrideshare.rideshare.controllers;

import com.marathonrideshare.rideshare.dto.*;
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
    public ResponseEntity<SearchRideResponse> searchRides(@RequestBody SearchRideRequest request) {
        SearchRideResponse response = rideQueryService.searchRides(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/rides/{rideId}/start")
    public ResponseEntity<StartRideResponse> startRide(@PathVariable String rideId, @RequestBody StartRideRequest request) {
        StartRideResponse startRideResponse = rideLifecycleService.startRide(rideId, request.getStartTime());
        return ResponseEntity.ok(startRideResponse);
    }

    @PatchMapping("/rides/{rideId}/complete")
    public ResponseEntity<CompleteRideResponse> completeRide(@PathVariable String rideId, @RequestBody CompleteRideRequest request) {
        CompleteRideResponse completeRideResponse = rideLifecycleService.completeRide(rideId, request.getCompletionTime());
        return ResponseEntity.ok(completeRideResponse);
    }

    @GetMapping("/rides/history/{userId}")
    public ResponseEntity<RideHistoryResponse> getRideHistory(@PathVariable String userId) {
        RideHistoryResponse response = rideQueryService.getRideHistory(userId);
        return ResponseEntity.ok(response);
    }

}
