/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Coordinates;
import com.luxoft.chainride.model.Follower;
import com.luxoft.chainride.model.Guidance;
import com.luxoft.chainride.model.Leader;
import java.sql.SQLException;
import java.util.List;
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
    
    public static final int GUIDANCE_THRESHOLD_M = 500;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SessionsResource
     */
    public SessionsResource() {
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
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GET
    @Path("leaders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Leader> listLeaders() {
        
        /*
        maintainLeaders(null);

        return new ArrayList<>(leaders.values());
        */

        try {
            SessionManager.maintainTables();

            return SessionManager.listLeaders();
        } catch (SQLException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GET
    @Path("{id}/follower")
    public Leader updateFollower(
            @PathParam("id") String id, 
            @QueryParam("lid") String leaderId, 
            @QueryParam("lat") double lat, 
            @QueryParam("lng") double lng) {
        
        try {
            SessionManager.maintainTables();
            
            SessionManager.updateFollower(id, leaderId, lat, lng);
            
            return SessionManager.getLeader(leaderId);
        } catch (SQLException ex) {
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
            @QueryParam("lng") double lng,
            @QueryParam("moved") boolean moved            
    ) {
        
        try {
            SessionManager.maintainTables();

            SessionManager.updateFollower(id, leaderId, lat, lng);

            Guidance g = new Guidance();

            Leader leader = SessionManager.getLeader(leaderId);
            g.setLeader(leader);
            
            if (leader!=null) {
                Coordinates myCoord = new Coordinates(lat, lng);
                if (myCoord.distanceToInM(leader.getLoc()) > GUIDANCE_THRESHOLD_M && moved) {
                    Guidance.addGuidance(g, myCoord, leader.getLoc());
                }
                return g;
            } else {
                System.out.println("Leader not found: " + leaderId);
                throw new WebApplicationException("LeaderId not found", Response.Status.BAD_REQUEST);
            }
        } catch (SQLException ex) {
            throw new WebApplicationException(ex, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
