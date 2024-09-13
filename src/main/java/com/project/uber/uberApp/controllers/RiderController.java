package com.project.uber.uberApp.controllers;

import com.project.uber.uberApp.dto.*;
import com.project.uber.uberApp.services.DriverService;
import com.project.uber.uberApp.services.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
@Secured("ROLE_RIDER")  // so only user having role Rider Can access these Routes
public class RiderController {

    private final RiderService riderService;


    @PostMapping("/requestRide")
    public ResponseEntity<RideRequestDto> requestRide(@RequestBody RideRequestDto rideRequestDto)
    {
        return  ResponseEntity.ok(riderService.requestRide(rideRequestDto));
    }

    @PostMapping("/cancelRide/{rideId}")
    public ResponseEntity<RideDto> cancelRide(@PathVariable Long rideId)
    {
        return  ResponseEntity.ok(riderService.cancelRide(rideId));
    }

    @PostMapping("/rateDriver/")
    public ResponseEntity<DriverDto> rateDriver(@RequestBody RatingDto ratingDto)
    {
        return  ResponseEntity.ok(riderService.rateDriver(ratingDto.getRideId(), ratingDto.getRating()));
    }

    @GetMapping("/getMyProfile")
    public ResponseEntity<RiderDto> getMyProfile()
    {
        return ResponseEntity.ok(riderService.getMyProfile());
    }

    @GetMapping("/getMyRides")
    public ResponseEntity<Page<RideDto>> getAllMyRides(@RequestParam(defaultValue = "0", required = false) Integer pageNumber,
                                                       @RequestParam(defaultValue = "10", required = false) Integer pageSize)
    {
        // page number -> data of particular page number
        // page size -> no. of data show in single page
        // total element -> total count of data
        // total pages -> total count of pages after arrange data according to page Size;
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize,
                Sort.by( Sort.Direction.DESC, "createdTime", "id"));

        return ResponseEntity.ok(riderService.getAllMyRides(pageRequest));
    }

    @PostMapping("/rateDriver/{rideId}/{rating}")
    public ResponseEntity<DriverDto> rateDriver(@PathVariable Long rideId, @PathVariable Integer rating)
    {
        return ResponseEntity.ok(riderService.rateDriver(rideId, rating));
    }

}
