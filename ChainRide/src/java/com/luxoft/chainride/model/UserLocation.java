/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride.model;

/**
 *
 * @author CBaldenhofer
 */
public class UserLocation {

    public UserLocation() {
    }

    public UserLocation(long lastPing, Coordinates coord) {
        this.lastPing = lastPing;
        this.coord = coord;
    }

    private long lastPing;

    /**
     * Get the value of lastPing
     *
     * @return the value of lastPing
     */
    public long getLastPing() {
        return lastPing;
    }

    /**
     * Set the value of lastPing
     *
     * @param lastPing new value of lastPing
     */
    public void setLastPing(long lastPing) {
        this.lastPing = lastPing;
    }

    private Coordinates coord;

    /**
     * Get the value of coord
     *
     * @return the value of coord
     */
    public Coordinates getCoord() {
        return coord;
    }

    /**
     * Set the value of coord
     *
     * @param coord new value of coord
     */
    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

}
