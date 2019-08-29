/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride.model;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import java.io.Serializable;

/**
 *
 * @author CBaldenhofer
 */
public class Coordinates implements Serializable {

    public Coordinates() {
    }

    public Coordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }
    
    public static Coordinates getCoordinates(double lat, double lng) {
        return new Coordinates(lat, lng);
    }
    
    public LatLng toLatLng() {
        return new LatLng(lat, lng);
    }
    
    public double distanceToInM(Coordinates coord) {
        if (coord==null) return Double.MAX_VALUE;
        double dist = LatLngTool.distance(toLatLng(), coord.toLatLng(), LengthUnit.METER);
        
        System.out.println("Distance " + toString() + " to " + coord.toString() + ": " + dist);
        
        return dist;
    }
    
    public String toHereFormat() {
        return lat + "," + lng;
    }

    @Override
    public String toString() {
        return "Coordinates{" + "lat=" + lat + ", lng=" + lng + '}';
    }
    
        private double lat;

    /**
     * Get the value of lat
     *
     * @return the value of lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * Set the value of lat
     *
     * @param lat new value of lat
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lng;

    /**
     * Get the value of lng
     *
     * @return the value of lng
     */
    public double getLng() {
        return lng;
    }

    /**
     * Set the value of lng
     *
     * @param lng new value of lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    
}
