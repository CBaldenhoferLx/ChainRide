/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Coordinates;
import com.luxoft.chainride.model.Guidance;
import java.util.HashMap;
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
    
    public static final Map<String, Coordinates> locations = new HashMap<>();

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
        locations.put(id, new Coordinates(lat, lng));
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Coordinates getLocation(@PathParam("id") String id) {
        if (locations.containsKey(id)) {
            return locations.get(id);
        } else {
            return null;
        }        
    }
    
    @GET
    @Path("guidance")
    @Produces(MediaType.APPLICATION_JSON)
    public Guidance calculateGuidance(
            @QueryParam("lid") String leaderId,
            @QueryParam("lat") double lat, 
            @QueryParam("lng") double lng) {
        if (locations.containsKey(leaderId)) {
            return Guidance.generateGuidance(new Coordinates(lat, lng), locations.get(leaderId));
        } else {
            System.out.println("Leader not found: " + leaderId);
            throw new WebApplicationException("LeaderId not found", Response.Status.BAD_REQUEST);
        }
    }
}
