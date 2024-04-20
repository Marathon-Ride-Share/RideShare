package com.marathonrideshare.rideshare.dto;

import com.marathonrideshare.rideshare.dto.LocationInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

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
    private LocationInfo origin;
    private LocationInfo destination;
    private Action action;
    private String userName;
}
