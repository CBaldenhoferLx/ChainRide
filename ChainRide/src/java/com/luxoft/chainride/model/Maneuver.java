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
public class Maneuver {
    
    public enum ManeuverType {
        LEFT,
        RIGHT
    }

    public Maneuver() {
    }

    public Maneuver(ManeuverType m, int d) {
        this.m = m;
        this.d = d;
    }
    
    private ManeuverType m;

    /**
     * Get the value of m
     *
     * @return the value of m
     */
    public ManeuverType getM() {
        return m;
    }

    /**
     * Set the value of m
     *
     * @param m new value of m
     */
    public void setM(ManeuverType m) {
        this.m = m;
    }

    
    private int d;

    /**
     * Get the value of d
     *
     * @return the value of d
     */
    public int getD() {
        return d;
    }

    /**
     * Set the value of d
     *
     * @param d new value of d
     */
    public void setD(int d) {
        this.d = d;
    }

    
}
