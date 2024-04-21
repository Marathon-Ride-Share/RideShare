package com.marathonrideshare.rideshare.repositories;

import com.marathonrideshare.rideshare.models.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByStatus(String status);
}
