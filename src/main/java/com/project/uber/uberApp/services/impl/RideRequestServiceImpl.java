package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.entities.RideRequest;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.RideRequestRepository;
import com.project.uber.uberApp.services.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;

    @Override
    public RideRequest findRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest Not Found With Id: "+rideRequestId));
    }

    //we update RideRequest Status When New Ride is Created
    @Override
    public void update(RideRequest rideRequest) {

        rideRequestRepository.findById(rideRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("RideRequest Not Found With Id "+rideRequest.getId()));

        rideRequestRepository.save(rideRequest);

    }
}
