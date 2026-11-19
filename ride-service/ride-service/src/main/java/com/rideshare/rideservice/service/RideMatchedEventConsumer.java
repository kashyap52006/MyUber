package com.rideshare.rideservice.service;

import com.rideshare.rideservice.event.RideMatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideMatchedEventConsumer {

    private final RideService rideService;

    @KafkaListener(topics = "ride.matched", groupId = "ride-service-group")
    public void consumeRideMatchedEvent(RideMatchedEvent event) {
        log.info("Received driver match for ride {}: driver {}",
                event.getRideId(), event.getDriverId());
        rideService.updateRideWithDriver(event.getRideId(), event.getDriverId());
    }
}
