package com.marathonrideshare.rideshare.repositories;

import com.marathonrideshare.rideshare.models.UserRides;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRidesRepository extends MongoRepository<UserRides, String> {
    @Query("{ '_id' : ?0 }")
    UserRides findUserRidesByUserName(String userName);
}
