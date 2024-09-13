package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
@Secured("ROLE_DRIVER")  // so only user having role Driver Can access these Routes
public class DriverController {

    private final DriverService driverService;

    @PostMapping("/acceptRide/{rideRequestId}")
    public ResponseEntity<RideDto> acceptRide(@PathVariable Long rideRequestId)
    {
        return ResponseEntity.ok(driverService.acceptRide(rideRequestId));
    }

    @PostMapping("/startRide/{rideId}")
    public ResponseEntity<RideDto> startRide(@PathVariable Long rideId, @RequestBody RideStartDto rideStartDto)
    {
        return ResponseEntity.ok(driverService.startRide(rideId, rideStartDto.getOtp()));
    }

    @PostMapping("/endRide/{rideId}")
    public ResponseEntity<RideDto> endRide(@PathVariable Long rideId)
    {
        return ResponseEntity.ok(driverService.endRide(rideId));
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId)
    {
        return  ResponseEntity.ok(driverService.cancelRide(rideId));
    }

    @PostMapping("/rateRider/")
    public ResponseEntity<RiderDto> rateRider(@RequestBody RatingDto ratingDto)
    {
        return  ResponseEntity.ok(driverService.rateRider(ratingDto.getRideId(), ratingDto.getRating()));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<DriverDto> getMyProfile()
    {
        return ResponseEntity.ok(driverService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0", required = false) Integer pageNumber,
                                                       @RequestParam(defaultValue = "10", required = false) Integer pageSize)
    {
        // page number -> data of particular page number
        // page size -> no. of data show in single page
        // total element -> total count of data
        // total pages -> total count of pages after arrange data according to page Size;
        // Sort -> we sort Desc on basic of createdTime, if created time is same then further sort by id
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,
                Sort.by( Sort.Direction.DESC, "createdTime", "id"));

        return ResponseEntity.ok(driverService.getAllMyRides(pageRequest));
    }

    @PostMapping("/rateRider/{rideId}/{rating}")
    public ResponseEntity<RiderDto> rateRider(@PathVariable Long rideId, @PathVariable Integer rating)
    {
        return ResponseEntity.ok(driverService.rateRider(rideId, rating));
    }
}
