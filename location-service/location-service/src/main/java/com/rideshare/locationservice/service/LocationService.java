package com.rideshare.locationservice.service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis key for all driver locations
    private static final String DRIVER_GEO_KEY = "drivers:locations";

    // Update driver location in Redis (called every 3 seconds by driver's app)
    // Maps to Redis GEOADD command
    public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
        log.info("Updating location for driver: {}", driverLocationRequest.getDriverId());

        // Always longitude (X) first and latitude (Y) second
        Point driverPoint = new Point(
                driverLocationRequest.getLongitude(),
                driverLocationRequest.getLatitude()
        );

        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY,
                driverPoint,
                driverLocationRequest.getDriverId()
        );

        log.info("Location updated for driver: {}", driverLocationRequest.getDriverId());
    }

    // Find nearby drivers within radius (called by matching service on ride request)
    // Maps to Redis GEORADIUS / GEOSEARCH command
    public List<NearByDriverResponse> findNearByDrivers(double latitude, double longitude, double radiusInKm) {
        log.info("Finding drivers near lat: {}, long: {} within {} km radius", latitude, longitude, radiusInKm);

        Circle searchArea = new Circle(
                new Point(longitude, latitude),
                new Distance(radiusInKm, Metrics.KILOMETERS)
        );

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVER_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10)
                );

        List<NearByDriverResponse> nearByDrivers = new ArrayList<>();

        if (results != null) {
            results.getContent().forEach(geoResult -> {
                RedisGeoCommands.GeoLocation<String> location = geoResult.getContent();
                nearByDrivers.add(
                        new NearByDriverResponse(
                                location.getName(),
                                location.getPoint().getY(), // Latitude (Verify order against your DTO!)
                                location.getPoint().getX(), // Longitude (Verify order against your DTO!)
                                geoResult.getDistance().getValue()
                        )
                );
            });
        }

        log.info("Found drivers nearby: {}", nearByDrivers.size());
        return nearByDrivers;
    }

    // Remove driver when they go offline
    // Maps to Redis ZREM command
    public void removeDriver(String driverId) {
        log.info("Removing driver: {}", driverId);
        redisTemplate.opsForGeo().remove(DRIVER_GEO_KEY, driverId);
    }
}