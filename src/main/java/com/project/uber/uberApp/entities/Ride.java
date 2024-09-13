package com.project.uber.uberApp.entities;


import com.project.uber.uberApp.entities.enums.PaymentMethod;
import com.project.uber.uberApp.entities.enums.RideStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_ride_rider", columnList = "rider_id"),
        @Index(name = "idx_ride_driver", columnList = "driver_id")
})
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "Geometry(Point, 4326)") // it refers to Earth Location
    private Point pickupLocation;

    @Column(columnDefinition = "Geometry(Point, 4326)") // it refers to Earth Location
    private Point dropOffLocation;

    @CreationTimestamp   //used to fill (currentTime) automatic when field is created
    private LocalDateTime createdTime;   // updated when driver accept our ride

    @ManyToOne(fetch = FetchType.LAZY)  // means until we not need this field, it not gives its value
    private Rider rider;

    @ManyToOne(fetch = FetchType.LAZY)  // means until we not need this field, it not gives its value
    private Driver driver;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private RideStatus rideStatus;

    private String otp;

    private Double fare;

    private LocalDateTime startedAt; // update when driver start our ride

    private LocalDateTime endedAt;  // update when driver end our ride



}
