package com.project.uber.uberApp.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")  //rider table have field called user_id, and this will be foreign key for User entity
    private User user;    //make one to one mapping with USER Entity

    private Double rating;

}
