package com.marathonrideshare.rideshare.dto;

import com.marathonrideshare.rideshare.shared.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class KafkaChatGroupEvent {
    public enum Action {
        CREATE,
        ADD_USER,
        DELETE
    }

    private String rideId;
    private Location origin;
    private Location destination;
    private Action action;
    private String userName;
}
