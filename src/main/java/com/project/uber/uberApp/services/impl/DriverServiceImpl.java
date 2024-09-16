package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RideDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.RideRequest;
import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.entities.enums.RideRequestStatus;
import com.project.uber.uberApp.entities.enums.RideStatus;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.services.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final RideRequestService rideRequestService;
    private final DriverRepository driverRepository;
    private final RideService rideService;
    private final ModelMapper modelMapper;
    private final PaymentService paymentService;
    private final RatingService ratingService;

    @Override
    @Transactional
    public RideDto acceptRide(Long rideRequestId) {

        //first check Requested Ride is Already Accepted or Not
        RideRequest rideRequest = rideRequestService.findRideRequestById(rideRequestId);

        //if status is not PENDING then means already rideRequest accepted by other driver
        if(!rideRequest.getRideRequestStatus().equals(RideRequestStatus.PENDING))
        {
            throw new RuntimeException("Ride Request Can Not Be Accepted, Status is: "+rideRequest.getRideRequestStatus());
        }

        //check Driver is in State of Available To Accept ride
        Driver currentDriver = getCurrentDriver();
        if(!currentDriver.getAvailable())
        {
           throw new RuntimeException("Driver Can Not Accept Ride Due to Unavailability");
        }

        //make Current Driver Availability to False and save it
        Driver savedDriver = updateDriverAvailability(currentDriver, false);

        // Now Create Ride Object Using Ride Service (Create New Ride)
        Ride ride = rideService.createNewRide(rideRequest, savedDriver);

        // map to RideDto and Return
        return modelMapper.map(ride, RideDto.class);
    }

    @Override
    public RideDto cancelRide(Long rideId) {

        //get Ride Details First
        Ride ride = rideService.getRideById(rideId);

        //check this driver Owns This Ride Or not
        Driver driver = getCurrentDriver();
        if(!driver.equals(ride.getDriver()))
            throw new RuntimeException("Driver Can Not Cancel This Ride, AS He Not Accepted It Earlier");

        //check Ride Status : It should Not be Ongoing,Ended,Canceled , if we want to cancel the ride it should be Confirmed
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED))
            throw new RuntimeException("Ride Can not Be Canceled, Invalid Status: "+ride.getRideStatus());

        //finally Cancel the Ride
        rideService.updateRideStatus(ride,RideStatus.CANCELLED);

        //make Driver Availability To True and Save it
        updateDriverAvailability(driver, true);

        //map to Ride DTO and Return
        return modelMapper.map(ride, RideDto.class);
    }

    @Override
    public RideDto startRide(Long rideId, String otp) {

        //get Ride Details first
        Ride ride = rideService.getRideById(rideId);

        //check this Driver Owns This Ride or Not
        Driver driver = getCurrentDriver();
        if(!driver.equals(ride.getDriver()))
            throw new RuntimeException("Driver Can Not Accept This Ride, AS He Not Accepted It Earlier");

        //check ride status not Canceled, Ended, Ongoing, It should be Confirmed
        if(!ride.getRideStatus().equals(RideStatus.CONFIRMED))
            throw new RuntimeException("Ride Status is Not CONFIRMED, Hence Can Not Be Started, Status: "+ride.getRideStatus());

        //check OTP now
        if(!otp.equals(ride.getOtp()))
            throw new RuntimeException("OTP is Not Valid, OTP: "+otp);

        //update start time of ride
        ride.setStartedAt(LocalDateTime.now());

        //Now Update Ride Status TO ONGOING
        Ride savedRide =  rideService.updateRideStatus(ride, RideStatus.ONGOING);

        // Create Payment Object for this Ride
        paymentService.createNewPayment(savedRide);

        //create Rating Object for this Ride
        ratingService.createNewRating(savedRide);

        return modelMapper.map(savedRide, RideDto.class);
    }

    @Override
    @Transactional
    public RideDto endRide(Long rideId) {

        //get Ride Details first
        Ride ride = rideService.getRideById(rideId);

        //check this Driver Owns This Ride or Not
        Driver driver = getCurrentDriver();
        if(!driver.equals(ride.getDriver()))
            throw new RuntimeException("Driver Can Not Accept This Ride, AS He Not Accepted It Earlier");

        //check ride status not Canceled, Ended, It should be Confirmed
        if(!ride.getRideStatus().equals(RideStatus.ONGOING ))
            throw new RuntimeException("Ride Status is Not ONGOING, Hence Can Not Be Ended, Status: "+ride.getRideStatus());

        //update end time for ride
        ride.setEndedAt(LocalDateTime.now());

        //end ride
       Ride savedRide =  rideService.updateRideStatus(ride, RideStatus.ENDED);

        //update driver availability to true
        updateDriverAvailability(driver, true);

        //take payment and update wallet or cash
        paymentService.processPayment(ride);

        return modelMapper.map(savedRide, RideDto.class) ;
    }

    @Override
    public RiderDto rateRider(Long rideId, Integer rating) {

        //get Ride first
        Ride ride = rideService.getRideById(rideId);

        //check this Driver Owns This Ride or Not
        Driver driver = getCurrentDriver();
        if(!driver.equals(ride.getDriver()))
            throw new RuntimeException("Driver is Not Owner of this Ride with Id: "+rideId);

        //check ride status should be ENDED only to rate
        if(!ride.getRideStatus().equals(RideStatus.ENDED))
            throw new RuntimeException("Ride Status is not Ended, Hence cannot start rating, Status: "+ride.getRideStatus());

        // now allow driver to rate rider and return
        return ratingService.rateRider(ride, rating);
    }

    @Override
    public DriverDto getMyProfile() {

        //get current Driver
        Driver currentDriver = getCurrentDriver();

        return modelMapper.map(currentDriver,DriverDto.class);
    }

    @Override
    public Page<RideDto> getAllMyRides(PageRequest pageRequest) {

        //get current Driver
        Driver currentDriver = getCurrentDriver();

        return rideService.getAllRidesOfDriver(currentDriver, pageRequest).map(
                ride -> modelMapper.map(ride, RideDto.class)
        );
    }

    @Override
    public Driver getCurrentDriver() {

        //get user using Spring Security Context
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return driverRepository.findByUser(user)
                .orElseThrow( () -> new ResourceNotFoundException("Driver not associated with User with Id: " +user.getId()));

    }

    @Override
    public Driver updateDriverAvailability(Driver driver, boolean available) {

        //update driver Availability
        driver.setAvailable(available);

        //save in repository
         return driverRepository.save(driver);
    }

    @Override
    public Driver createNewDriver(Driver driver) {

        return driverRepository.save(driver);
    }
}
