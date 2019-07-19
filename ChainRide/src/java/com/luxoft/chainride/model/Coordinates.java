/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride.model;

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
    
    public String toHereFormat() {
        return lat + "," + lng;
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
