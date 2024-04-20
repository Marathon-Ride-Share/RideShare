package com.marathonrideshare.rideshare.repository;

import com.marathonrideshare.rideshare.models.UserRides;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRidesRepository extends MongoRepository<UserRides, String> {
}
