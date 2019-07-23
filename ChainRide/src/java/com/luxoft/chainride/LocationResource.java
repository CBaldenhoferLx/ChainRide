/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Coordinates;
import com.luxoft.chainride.model.Guidance;
import com.luxoft.chainride.model.UserLocation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Megaport
 */
@Path("loc")
public class LocationResource {
    
    private final static int MAX_LIFETIME_MS = 30 * 1000;

    public static final Map<String, UserLocation> locations = new HashMap<>();
    
    private synchronized void maintainLocations(final String id, final double lat, final double lng) {
        Iterator<String> it = locations.keySet().iterator();

        while(it.hasNext()) {
            String name = it.next();
            if (System.currentTimeMillis() - locations.get(name).getLastPing() > MAX_LIFETIME_MS) {
                it.remove();
            }
        }

        locations.put(id, new UserLocation(System.currentTimeMillis(), new Coordinates(lat, lng)));
    }

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of LocationResource
     */
    public LocationResource() {
    }

    @GET
    @Path("{id}/update")
    public void updateLocation(
            @PathParam("id") String id, 
            @QueryParam("lat") double lat, 
            @QueryParam("lng") double lng) {
        System.out.println(id);
        
        maintainLocations(id, lat, lng);
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Coordinates getLocation(@PathParam("id") String id) {
        if (locations.containsKey(id)) {
            return locations.get(id).getCoord();
        } else {
            return null;
        }        
    }
    
    @GET
    @Path("{id}/guidance")
    @Produces(MediaType.APPLICATION_JSON)
    public Guidance calculateGuidance(
            @PathParam("id") String id,
            @QueryParam("lid") String leaderId,
            @QueryParam("lat") double lat, 
            @QueryParam("lng") double lng) {
        
        maintainLocations(id, lat, lng);
        
        if (locations.containsKey(leaderId)) {
            return Guidance.generateGuidance(new Coordinates(lat, lng), locations.get(leaderId).getCoord());
        } else {
            System.out.println("Leader not found: " + leaderId);
            throw new WebApplicationException("LeaderId not found", Response.Status.BAD_REQUEST);
        }
    }
}
