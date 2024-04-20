package com.marathonrideshare.rideshare.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "userRides")
public class UserRides {
    @Id
    private String userName;
    private List<RideAssociation> rides;
}


