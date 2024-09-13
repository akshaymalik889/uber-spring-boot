package com.project.uber.uberApp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
        @Index(name = "idx_rating_rider", columnList = "rider_id"),
        @Index(name = "idx_rating_driver", columnList = "driver_id")
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @OneToOne
    private Ride ride;  // one ride can have only one rating

    @ManyToOne
    private Rider rider;   // one rider can have multiple ratings

    @ManyToOne
    private Driver driver;   // one driver can have multiple ratings

    private Integer driverRating;  //rating for the Driver

    private Integer riderRating;  //rating for the Rider
}
