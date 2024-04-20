package com.marathonrideshare.rideshare.repositories;

import com.marathonrideshare.rideshare.models.Ride;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RideRepository extends MongoRepository<Ride, String> {
}
