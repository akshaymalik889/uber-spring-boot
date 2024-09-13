package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.services.DistanceService;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DistanceServiceOSRMImpl implements DistanceService {

    private static final String OSRM_API_BASE_URL = "http://router.project-osrm.org/route/v1/driving/";

    //Call the Third Party API Called: OSRM to Calculate Distance
    @Override
    public double calculateDistance(Point src, Point dest) {

        //create  and add uri after OSRM base URL after put our longitude and latitude of Source and Destination
        String uri = src.getX()+","+src.getY()+";"+dest.getX()+","+dest.getY();
        try {
            // create object of RestClient
            OSRMResponseDto responseDto = RestClient.builder()
                    .baseUrl(OSRM_API_BASE_URL)
                    .build() // get Rest Client Instance
                    .get()  // Get request on URL
                    .uri(uri) //After than Add uri To BASE URL
                    .retrieve()  // Get all Data from Api
                    .body(OSRMResponseDto.class);  // convert all data to OSRMResponseDto type


            //we go to routes -> then get first route -> then get distance -> then divide by 1000 to get in KMs
            return responseDto.getRoutes().get(0).getDistance() / 1000.0;

        }catch (Exception e){
            throw new RuntimeException("Error Getting Data From OSRM "+e.getMessage());
        }
    }
}

//Create DTO for OSRM api -> only interested in Routes field Array
@Data
class OSRMResponseDto
{
    private List<OSRMRoute> routes;
}

//in Route Field We only want distance
@Data
class OSRMRoute
{
    private Double distance;
}
