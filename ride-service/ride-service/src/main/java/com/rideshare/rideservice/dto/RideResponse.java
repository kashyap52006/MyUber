package com.rideshare.rideservice.dto;

import com.rideshare.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideResponse {
    private String id;
    //who reqeusted the ride
    private String riderId;
    //who accepted the ride
    private String driverId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
    // ride status, tracks the live status (lifecycle)
    private RideStatus status;
    //Fare detail
    private double estimatedFare;
    private double actualFare;
    //Time stemps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
