package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RideRequestDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.entities.*;
import com.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.project.uber.uberApp.entities.enums.RideStatus;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRequestRepository;
import com.project.uber.uberApp.repositories.RiderRepository;
import com.project.uber.uberApp.services.DriverService;
import com.project.uber.uberApp.services.RatingService;
import com.project.uber.uberApp.services.RideService;
import com.project.uber.uberApp.services.RiderService;
import com.project.uber.uberApp.strategies.RideStrategyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RiderServiceImpl implements RiderService {

    private final ModelMapper modelMapper;
    private final RideStrategyManager rideStrategyManager;
    private final RideRequestRepository rideRequestRepository;
    private final RiderRepository riderRepository;
    private final RideService rideService;
    private final DriverService driverService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideRequestDto requestRide(RideRequestDto rideRequestDto) {

        Rider rider = getCurrentRider();

        //converting rideRequestDto to rideRequest Object
        // for Point we convert in Model Mapper using Geometry factory (PointDto to Point)
        RideRequest rideRequest = modelMapper.map(rideRequestDto, RideRequest.class);

        //site Ride Request Status to Pending
        rideRequest.setRideRequestStatus(RideRequestStatus.PENDING);

        // store rider in ride request
        rideRequest.setRider(rider);

        //calculate fare
        Double fare = rideStrategyManager.rideFareCalculationStrategy().calculateFare(rideRequest);
        rideRequest.setFare(fare);

        //save ride Request in Ride Request Repository
        RideRequest savedRideRequest = rideRequestRepository.save(rideRequest);

        //Finding Drivers
        List<Driver> drivers = rideStrategyManager.driverMatchingStrategy(rider.getRating()).findMatchingDriver(rideRequest);

        //TODO:Send Notification to All Drivers about this ride Request (Email)

        //return ride requestDto
        return modelMapper.map(savedRideRequest, RideRequestDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {

        //get Current Rider
        Rider rider = getCurrentRider();

        //get Current Ride Detail
        Ride ride = rideService.getRideById(rideId);

        //checking the Ride Rider belongs to this Rider
        if(!rider.equals(ride.getRider()))
            throw  new RuntimeException("Rider Does Not Own this Ride with Ride Id: "+rideId);


        //check Ride Status : It should Not be Ongoing,Ended,Canceled , if we want to cancel the ride it should be Confirmed
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED))
            throw new RuntimeException("Ride Can not Be Canceled, Invalid Status: "+ride.getRideStatus());

        //finally Cancel the Ride
        Ride savedRide = rideService.updateRideStatus(ride,RideStatus.CANCELLED);

        //make Driver Availability To True (we find driver details from Ride Object)
        driverService.updateDriverAvailability(ride.getDriver(), true);

        //map to Ride DTO and Return
        return modelMapper.map(savedRide, RideDto.class);
    }

    @Override
    public DriverDto rateDriver(Long rideId, Integer rating) {

        //get Ride first
        Ride ride = rideService.getRideById(rideId);

        //check this Rider Owns This Ride or Not
        Rider rider = getCurrentRider();
        if(!rider.equals(ride.getRider()))
            throw new RuntimeException("Rider is Not Associated to this Ride with Id: "+rideId);

        //check ride status should be ENDED only to rate
        if(!ride.getRideStatus().equals(RideStatus.ENDED))
            throw new RuntimeException("Ride Status is not Ended, Hence cannot start rating, Status: "+ride.getRideStatus());

        // now allow driver to rate rider and return
        return ratingService.rateDriver(ride, rating);
    }

    @Override
    public RiderDto getMyProfile() {

        //get current Rider
        Rider currentRider = getCurrentRider();

        return modelMapper.map(currentRider, RiderDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {

        //get current Rider
        Rider currentRider = getCurrentRider();

        //call method of Ride Service , to get ALl rides of particular Rider, and map it to RideDTO
        return rideService.getAllRidesOfRider(currentRider, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Rider createNewRider(User user) {

        Rider rider = Rider
                .builder()
                .user(user)
                .rating(0.0)
                .build();

        return riderRepository.save(rider);

    }

    @Override
    public Rider getCurrentRider() {

        //get user using Spring Security Context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //now we find the rider with this user id
        return riderRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not associated with User with Id "+user.getId()));
    }
}
