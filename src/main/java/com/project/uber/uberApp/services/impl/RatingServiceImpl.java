package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.dto.DriverDto;
import com.project.uber.uberApp.dto.RiderDto;
import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Rating;
import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.Rider;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.exceptions.RuntimeConflictException;
import com.project.uber.uberApp.repositories.DriverRepository;
import com.project.uber.uberApp.repositories.RatingRepository;
import com.project.uber.uberApp.repositories.RiderRepository;
import com.project.uber.uberApp.services.RatingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final DriverRepository driverRepository;
    private final RiderRepository riderRepository;
    private final ModelMapper modelMapper;


    @Override
    public DriverDto rateDriver(Ride ride, Integer rating) {

        //get Driver
        Driver driver = ride.getDriver();

        //find rating for particular ride
        Rating ratingObj = ratingRepository.findByRide(ride)
                .orElseThrow( () -> new ResourceNotFoundException("Rating Not Found For Ride With Id: "+ride.getId()));

        //check driver is already rated or not
        if(ratingObj.getDriverRating() != null)
            throw new RuntimeConflictException("Driver has Already been Rated, can not Rate Again");

        //set rating for driver
        ratingObj.setDriverRating(rating);

        //save rating in rating repository
        ratingRepository.save(ratingObj);

        //get all the ratings of particular driver and then calculate average for all rating
        Double newRating = ratingRepository.findByDriver(driver)// find all rating for a driver
                .stream()
                .mapToDouble(rating1 -> rating1.getDriverRating()) // make it double
                .average() // find average for all rating
                .orElse(0.0);

        //set rating for driver
        driver.setRating(newRating);
        //save driver
        Driver savedDriver = driverRepository.save(driver);

        return modelMapper.map(savedDriver, DriverDto.class);
    }

    @Override
    public RiderDto rateRider(Ride ride, Integer rating) {

        //get Rider
        Rider rider = ride.getRider();

        //find ratingObject for particular ride (as we create Rating Object in ride while,driver Start Ride )
        Rating ratingObj = ratingRepository.findByRide(ride)
                .orElseThrow( () -> new ResourceNotFoundException("Rating Not Found For Ride With Id: "+ride.getId()));

        //check Rider is already rated or not
        if(ratingObj.getRiderRating() != null)
            throw new RuntimeConflictException("Rider has Already been Rated, can not Rate Again");

        //set rating for rider
        ratingObj.setRiderRating(rating);

        //save rating in rating repository
        ratingRepository.save(ratingObj);

        //get all the ratings of particular rider and then calculate average for all rating
        Double newRating = ratingRepository.findByRider(rider)// find all rating for a rider
                .stream()
                .mapToDouble(rating1 -> rating1.getRiderRating()) // make it double
                .average() // find average for all rating
                .orElse(0.0);

        //set rating for rider
        rider.setRating(newRating);
        //save rider
        Rider savedRider = riderRepository.save(rider);

        return modelMapper.map(savedRider, RiderDto.class);

    }

    @Override
    public void createNewRating(Ride ride) {

        Rating rating = Rating.builder()
                .rider(ride.getRider())
                .driver(ride.getDriver())
                .ride(ride)
                .build();

        ratingRepository.save(rating);
    }
}
