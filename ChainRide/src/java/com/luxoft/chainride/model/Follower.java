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
public class Follower {

    public Follower() {
    }

    public Follower(String id, Coordinates loc) {
        this.id = id;
        this.loc = loc;
    }

    private String id;

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(String id) {
        this.id = id;
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

}
