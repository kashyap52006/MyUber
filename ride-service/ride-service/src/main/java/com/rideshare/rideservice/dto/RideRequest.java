package com.rideshare.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {

    @NotBlank(message = "rider id is required")
    private String riderId;

    @DecimalMin(value = "-90.0", message = "Pickup latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Pickup latitude must be between -90 and 90")
    @NotNull(message = "Pickup latitude is required")
    private Double pickupLatitude;
    @DecimalMin(value = "-180.0", message = "Pickup longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Pickup longitude must be between -180 and 180")
    @NotNull(message = "Pickup longitude is required")
    private Double pickupLongitude;
    @jakarta.validation.constraints.NotBlank(message = "Pickup address is required")
    private String pickupAddress;
    @DecimalMin(value = "-90.0", message = "Drop latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Drop latitude must be between -90 and 90")
    @NotNull(message = "Drop latitude is required")
    private Double dropLatitude;
    @DecimalMin(value = "-180.0", message = "Drop longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Drop longitude must be between -180 and 180")
    @NotNull(message = "Drop longitude is required")
    private Double dropLongitude;
    @jakarta.validation.constraints.NotBlank(message = "Drop address is required")
    private String dropAddress;


}
