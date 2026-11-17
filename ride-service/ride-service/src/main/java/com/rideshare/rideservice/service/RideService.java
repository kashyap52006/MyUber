package com.rideshare.rideservice.service;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.event.RideReqeustedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

    private RideRepository rideRepository;
    private KafkaTemplate<String , RideReqeustedEvent> kafkaTemplate;
    private static final String RIDE_REQUESTED_TOPIC = "ride.requested";

    /*
    create a ride DB with REQUESTED STATUS
     */

    public RideResponse requestRide(RideRequest request){
        log.info("New ride Request from rider : {}" , request.getRiderId());
        //step 1 - save ride to db
        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        ride.setStatus(RideStatus.REQUESTED);

        ride.setEstimatedFare(calculateEstimateFare(request));
        Ride saveRide = rideRepository.save(ride);

        // step 2 publish event to kafka
        //matching service will consume this and find nearest driver

        RideReqeustedEvent event = new RideReqeustedEvent(
                saveRide.getId(),
                saveRide.getRiderId(),
                saveRide.getPickupLatitude(),
                saveRide.getPickupLongitude(),
                saveRide.getPickupAddress(),
                saveRide.getDropLatitude(),
                saveRide.getDropLongitude(),
                saveRide.getDropAddress()
        );

        kafkaTemplate.send(RIDE_REQUESTED_TOPIC , saveRide.getId() , event);

        log.info("RideReqeustEvent published to kafka for ride : {}" , saveRide.getId());

        // Update status to matching

        saveRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(saveRide);
        return mapToResponse(saveRide);

    }

    // to update status to ACCEPTED when driver accepts the request
    public void updateRideWithDriver(String rideId , String DriverId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        ride.setDriverId(DriverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);

    }

    public RideResponse startRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getStatus() != RideStatus.ACCEPTED){
            throw new RuntimeException("Ride cannot be started. Current status : " + ride.getStatus());

        }
        ride.setStatus(RideStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);

        return mapToResponse(ride);

    }

    public RideResponse completeRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getStatus() != RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride cannot be completed. Current status : " + ride.getStatus());

        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());
        rideRepository.save(ride);

        return mapToResponse(ride);

    }

    public RideResponse cancelRide(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if(ride.getStatus() != RideStatus.RIDE_STARTED){
            throw new RuntimeException("Ride cannot be completed. Current status : " + ride.getStatus());

        }
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);

        return mapToResponse(ride);

    }


    public RideResponse getRideById(String rideId){
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()-> new RuntimeException("Ride not found"));

        return mapToResponse(ride);

    }

    public List<RideResponse> getRidesByRider(String riderId){
        return rideRepository.findByRiderIdOrderByCreatedDesc(riderId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());


    }
    private double calculateEstimateFare(RideRequest request){
        double lat1 = Math.toRadians(request.getPickupLatitude());
        double lat2 = Math.toRadians(request.getDropLatitude());
        double lon1 = Math.toRadians(request.getPickupLongitude());
        double lon2 = Math.toRadians(request.getDropLongitude());

        double dLat = lat2-lat1;
        double dLon = lon2 - lon2;

        double a =Math.pow(Math.sin(dLat / 2), 2)
                +Math.cos(lat1) * Math.cos(lat2)
                *Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double dustanceKm = 6371 * c;

        //Base fare: 50Rs + 12Rs. perKm
        double fare = 50 + (dustanceKm * 12);
        return Math.round(fare * 100.0) / 100.0;
    }

    private RideResponse mapToResponse(Ride ride){
        RideResponse response = new RideResponse();
        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());
        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());
        response.setPickupAddress(ride.getPickupAddress());
        response.setDropLatitude(ride.getDropLatitude());
        response.setDropLongitude(ride.getDropLongitude());
        response.setDropAddress(ride.getDropAddress());
        response.setStatus(ride.getStatus());
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setActualFare(ride.getActualFare());
        response.setCreatedAt(ride.getCreatedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());
        return response;
    }
}
