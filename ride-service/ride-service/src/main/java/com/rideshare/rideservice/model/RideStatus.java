package com.rideshare.rideservice.model;
/**
 * Flow : REQEUSTED -> MATCHING -> ACCEPTED - >
 *          RIDE STARTED -> COMPLETED->
 *          CANCELLED ( can happen at multiple stages)
 *
 */
public enum RideStatus {
    REQUESTED,
    MATCHING,
    ACCEPTED,
    DRIVER_ARRIVING,
    RIDE_STARTED,
    COMPLETED,
    CANCELLED
}
