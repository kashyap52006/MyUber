package com.rideshare.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
Event consumed from kafka topic : ride.requested
Publish by ride service when a rider requests a ride
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
