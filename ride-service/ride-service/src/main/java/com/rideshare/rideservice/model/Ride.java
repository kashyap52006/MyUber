package com.rideshare.rideservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "rides")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    //who reqeusted the ride
    @Column(nullable = false)
    private String riderId;

    //who accepted the ride
    @Column(nullable = false)
    private String driverId;

    @Column(nullable = false)
    private double pickupLatitude;

    @Column(nullable = false)
    private double pickupLongitude;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(nullable = false)
    private double dropLatitude;

    @Column(nullable = false)
    private double dropLongitude;

    @Column(nullable = false)
    private String dropAddress;

    // ride status, tracks the live status (lifecycle)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    //Fare detail
    private double estimatedFare;
    private double actualFare;

    //Time stemps
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;


}
