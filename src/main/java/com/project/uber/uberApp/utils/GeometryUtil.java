package com.project.uber.uberApp.utils;

import com.project.uber.uberApp.dto.PointDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtil {

    //convert PointDto to Point(geom.location)
    public static Point createPoint(PointDto pointDto)
    {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
                                                                //longitude       //latitude
        Coordinate  coordinate = new Coordinate(pointDto.getCoordinates()[0], pointDto.getCoordinates()[1]);

        //it converts coordinate to type Point then return
        return geometryFactory.createPoint(coordinate);
    }
}
