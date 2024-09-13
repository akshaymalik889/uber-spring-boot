package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.RideRequest;
import com.project.uber.uberApp.entities.Rider;
import com.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.project.uber.uberApp.entities.enums.RideStatus;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRepository;
import com.project.uber.uberApp.services.RideRequestService;
import com.project.uber.uberApp.services.RideService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Random;


@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RideRequestService rideRequestService;
    private final ModelMapper modelMapper;

    @Override
    public Ride getRideById(Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride Not Found With Id: "+rideId));
    }

    @Override
    public Ride createNewRide(RideRequest rideRequest, Driver driver) {

        //make rideRequest Status to Confirmed
        rideRequest.setRideRequestStatus(RideRequestStatus.CONFIRMED);

        //convert rideRequest to Ride Object because both have mostly common fields
        Ride ride = modelMapper.map(rideRequest, Ride.class);

        //set ride Status to Confirmed
        ride.setRideStatus(RideStatus.CONFIRMED);

        //set driver
        ride.setDriver(driver);

        //create an OTP and set OTP
        ride.setOtp(generateRandomOTP());

        //set ride id to null so there no id will set in mapping
        ride.setId(null);

        //update rideRequest Also , because we changed rideRequest Status to Confirmed
        rideRequestService.update(rideRequest);

        //saved ride in DB and then return
        return rideRepository.save(ride);
    }

    @Override
    public Ride updateRideStatus(Ride ride, RideStatus rideStatus) {

        ride.setRideStatus(rideStatus);
        return rideRepository.save(ride);
    }

    @Override
    public Page<Ride> getAllRidesOfRider(Rider rider, PageRequest pageRequest) {

        return rideRepository.findByRider(rider, pageRequest);
    }

    @Override
    public Page<Ride> getAllRidesOfDriver(Driver driver, PageRequest pageRequest) {
        return rideRepository.findByDriver(driver, pageRequest);
    }

    private String generateRandomOTP()
    {
        Random random = new Random();
        int otpInt = random.nextInt(10000);  // gives any random number from 0 to 9999
        //convert to string and return
        return String.format("%04d", otpInt);  // 04d means 4 digit
    }
}
