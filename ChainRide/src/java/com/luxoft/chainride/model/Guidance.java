/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride.model;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.luxoft.chainride.HereMapsConnector;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author CBaldenhofer
 */
public class Guidance {
    
    public Guidance() {
        maneuvers = new ArrayList<>();
    }
    
    public static Guidance generateGuidance(Coordinates follower, Coordinates leader) {
        Guidance g = new Guidance();
        
        g.setLd((int) LatLngTool.distance(new LatLng(follower.getLat(), follower.getLng()), new LatLng(leader.getLat(), leader.getLng()), LengthUnit.METER));
        HereMapsConnector.calculateSteps(follower, leader, g);
        
        return g;
    }    
    
    public void addManeuver(Maneuver m) {
        maneuvers.add(m);
    }
    
        private int ld;

    /**
     * Get the value of ld
     *
     * @return the value of ld
     */
    public int getLd() {
        return ld;
    }

    /**
     * Set the value of ld
     *
     * @param ld new value of ld
     */
    public void setLd(int ld) {
        this.ld = ld;
    }

    
    private List<Maneuver> maneuvers;

    /**
     * Get the value of maneuvers
     *
     * @return the value of maneuvers
     */
    public List<Maneuver> getManeuvers() {
        return maneuvers;
    }

    /**
     * Set the value of maneuvers
     *
     * @param maneuvers new value of maneuvers
     */
    public void setManeuvers(List<Maneuver> maneuvers) {
        this.maneuvers = maneuvers;
    }

    
    
}
