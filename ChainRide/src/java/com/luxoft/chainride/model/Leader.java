/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride.model;

import java.io.Serializable;

/**
 *
 * @author Megaport
 */
public class Leader implements Serializable {

    public Leader() {
    }
    
    public Leader(Coordinates loc, String name, long lastPing) {
        this.loc = loc;
        this.name = name;
        this.lastPing = lastPing;
    }
    
    
        private Coordinates loc;

    /**
     * Get the value of loc
     *
     * @return the value of loc
     */
    public Coordinates getLoc() {
        return loc;
    }

    /**
     * Set the value of loc
     *
     * @param loc new value of loc
     */
    public void setLoc(Coordinates loc) {
        this.loc = loc;
    }

    
        private String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
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

    
}
