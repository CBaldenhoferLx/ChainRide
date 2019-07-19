/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Leader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author CBaldenhofer
 */
@Path("sessions")
public class SessionsResource {
    
    //private final static int MAX_LIFETIME_MS = 60 * 1000;
    private final static int MAX_LIFETIME_MS = 20 * 1000;
    private final static Map<String, Leader> leaders = new HashMap<>();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SessionsResource
     */
    public SessionsResource() {
    }
    
    private synchronized void maintainLeaders(String newLeaderToAdd) {
        Iterator<String> it = leaders.keySet().iterator();
        
        while(it.hasNext()) {
            String name = it.next();
            if (System.currentTimeMillis() - leaders.get(name).getLastPing() > MAX_LIFETIME_MS) {
                it.remove();
            }
        }
        
        if (newLeaderToAdd!=null) {
            leaders.put(newLeaderToAdd, new Leader(newLeaderToAdd, System.currentTimeMillis()));
        }
    }

    @GET
    @Path("registerLeader")
    public void registerLeader(@QueryParam("id") String id) {
        if (!leaders.containsKey(id)) {
            System.out.println("Adding leader " + id);
        } else {
            System.out.println("Leader already exists");
        }
        
        maintainLeaders(id);

        System.out.println(leaders.toString());
    }
    
    @GET
    @Path("leaders")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Leader> listLeaders() {
        maintainLeaders(null);

        return new ArrayList<>(leaders.values());
    }
}
