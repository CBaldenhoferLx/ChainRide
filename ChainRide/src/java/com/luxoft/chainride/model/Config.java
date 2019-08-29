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
public class Config {

    public static final int GUIDANCE_THRESHOLD_M = 500;
    public static final int TBT_LIMIT = 3;

    public Config() {
    }
    
        private double guideThresholdM = GUIDANCE_THRESHOLD_M;

    /**
     * Get the value of guideThresholdM
     *
     * @return the value of guideThresholdM
     */
    public double getGuideThresholdM() {
        return guideThresholdM;
    }

    /**
     * Set the value of guideThresholdM
     *
     * @param guideThresholdM new value of guideThresholdM
     */
    public void setGuideThresholdM(double guideThresholdM) {
        this.guideThresholdM = guideThresholdM;
    }
    
    private int tbtLimit = TBT_LIMIT;

    /**
     * Get the value of tbtLimit
     *
     * @return the value of tbtLimit
     */
    public int getTbtLimit() {
        return tbtLimit;
    }

    /**
     * Set the value of tbtLimit
     *
     * @param tbtLimit new value of tbtLimit
     */
    public void setTbtLimit(int tbtLimit) {
        this.tbtLimit = tbtLimit;
    }


    
}
