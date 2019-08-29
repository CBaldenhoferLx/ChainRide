/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride.model;

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
    
    public static void addGuidance(Guidance g, Coordinates follower, Coordinates leader) {
        HereMapsConnector.calculateSteps(follower, leader, g);
    }

    @Override
    public String toString() {
        return "Guidance{" + "leader=" + leader + ", maneuvers=" + maneuvers + '}';
    }
    
    private String nextInstr = "";

    /**
     * Get the value of nextInstr
     *
     * @return the value of nextInstr
     */
    public String getNextInstr() {
        return nextInstr;
    }

    /**
     * Set the value of nextInstr
     *
     * @param nextInstr new value of nextInstr
     */
    public void setNextInstr(String nextInstr) {
        this.nextInstr = nextInstr;
    }

   
    private Leader leader;

    /**
     * Get the value of leader
     *
     * @return the value of leader
     */
    public Leader getLeader() {
        return leader;
    }

    /**
     * Set the value of leader
     *
     * @param leader new value of leader
     */
    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    
    public void addManeuver(Maneuver m) {
        maneuvers.add(m);
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
