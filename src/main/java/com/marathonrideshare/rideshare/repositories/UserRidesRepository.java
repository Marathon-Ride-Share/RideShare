package com.marathonrideshare.rideshare.repositories;

import com.marathonrideshare.rideshare.models.UserRides;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRidesRepository extends MongoRepository<UserRides, String> {
}
