package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/*
    event published to kafka when a ride is reqeusted
    matching service consumes this event
    TOPIC : ride.requested
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {

    private String rideId;
    private String riderId;

    // pickup
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;

    // Drop

    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;


}
