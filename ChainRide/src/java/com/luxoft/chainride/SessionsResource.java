/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Config;
import com.luxoft.chainride.model.Coordinates;
import com.luxoft.chainride.model.Follower;
import com.luxoft.chainride.model.Guidance;
import com.luxoft.chainride.model.Leader;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author CBaldenhofer
 */
@Path("sessions")
public class SessionsResource {
    
    public static final int GUIDANCE_LAST_COORD_THRESHOLD_M = 200;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SessionsResource
     */
    public SessionsResource() {
    }
    
    @GET
    @Path("config")
    @Produces(MediaType.APPLICATION_JSON)
    public Config getConfig() {
        return new Config();
    }

    @GET
    @Path("{id}/leader")
    public List<Follower> updateLeader(
            @PathParam("id") String id,
            @QueryParam("lat") double lat,
            @QueryParam("lng") double lng
            ) {

        try {
            SessionManager.maintainTables();

            SessionManager.updateLeader(id, lat, lng);
            
            return SessionManager.listFollowers(id);
        } catch (SQLException ex) {
            Logger.getLogger(SessionsResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
        
    }
    
    @GET
    @Path("leaders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Leader> listLeaders() {
        try {
            SessionManager.maintainTables();

            return SessionManager.listLeaders();
        } catch (SQLException ex) {
            Logger.getLogger(SessionsResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GET
    @Path("{id}/follow")
    @Produces(MediaType.APPLICATION_JSON)
    public Guidance calculateGuidance(
            @PathParam("id") String id,
            @QueryParam("lid") String leaderId,
            @QueryParam("lat") double lat, 
            @QueryParam("lng") double lng
    ) {
        try {
            SessionManager.maintainTables();

            SessionManager.updateFollower(id, leaderId, lat, lng);

            final Guidance g = new Guidance();

            final Leader leader = SessionManager.getLeader(leaderId);
            
            if (leader!=null) {
                System.out.println("Leader " + leader.toString());

                g.setLeader(leader);

                Coordinates myCoord = new Coordinates(lat, lng);
                if (myCoord.distanceToInM(leader.getLoc()) > Config.GUIDANCE_THRESHOLD_M) {
                    
                    final Coordinates lastGuidance = SessionManager.getLastGuidance(id);
                    
                    if (myCoord.distanceToInM(lastGuidance) > GUIDANCE_LAST_COORD_THRESHOLD_M) {
                        System.out.println("Calculating guidance for " + id);
                        
                        try {
                            Guidance.addGuidance(g, myCoord, leader.getLoc());

                            SessionManager.updateLastGuidance(id, myCoord);
                        } catch(Exception ex) {
                            Logger.getLogger(SessionsResource.class.getName()).log(Level.SEVERE, null, ex);
                            throw new WebApplicationException("LeaderId not found", Response.Status.BAD_REQUEST);
                        }
                    } else {
                        System.out.println(id + " has not moved enough");
                    }
                } else {
                    System.out.println(id + " is below distance");
                }
                
                return g;
            } else {
                System.out.println("Leader not found: " + leaderId);
                throw new WebApplicationException("LeaderId not found", Response.Status.BAD_REQUEST);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SessionsResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
