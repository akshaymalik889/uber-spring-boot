package com.project.uber.uberApp.repositories;

import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Rating;
import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRider(Rider rider); // gives list of all riders rating
    List<Rating> findByDriver(Driver driver); // gives list of all drivers rating


    Optional<Rating> findByRide(Ride ride); // gives rating on basis of particular ride
}
