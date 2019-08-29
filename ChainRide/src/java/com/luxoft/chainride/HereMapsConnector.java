/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Config;
import com.luxoft.chainride.model.Coordinates;
import com.luxoft.chainride.model.Guidance;
import com.luxoft.chainride.model.Maneuver;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author CBaldenhofer
 */
public class HereMapsConnector {
    
    final public static String APP_ID = "ESPwssJntvck2qjU7JgS";
    final public static String APP_CODE = "MQlFVvG9zrnQeFItqpZ0zQ";
    final public static String REST_URI = "https://route.api.here.com/routing/7.2/calculateroute.json";
    
    public static void calculateSteps(Coordinates start, Coordinates end, Guidance g) {
        
        Logger.getLogger(HereMapsConnector.class.getName()).log(Level.INFO, "HERE Request: {0} to {1}", new Object[]{start.toString(), end.toString()});

        final Client client = ClientBuilder.newClient();

        String output = client.target(REST_URI)
                .queryParam("waypoint0", start.toHereFormat())
                .queryParam("waypoint1", end.toHereFormat())
                .queryParam("mode", "fastest;car;traffic:enabled")
                .queryParam("maneuverattributes", "ac")
                .queryParam("app_id", APP_ID)
                .queryParam("app_code", APP_CODE)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        
        System.out.println("HERE Response: " + output);
        
        JSONObject obj = new JSONObject(output);
        JSONObject response = obj.optJSONObject("response");
        
        JSONArray routes = response.optJSONArray("route");
        
        if (!routes.isEmpty()) {
            JSONObject firstRoute = routes.optJSONObject(0);
            
            JSONArray legs = firstRoute.optJSONArray("leg");                    
            
            if (!legs.isEmpty()) {
                
                JSONObject firstLeg = legs.optJSONObject(0);
                
                JSONArray maneuvers = firstLeg.optJSONArray("maneuver");
                
                for (int i=0;i<maneuvers.length();i++) {
                    if (i>=Config.TBT_LIMIT) break;
                    
                    JSONObject maneuver = maneuvers.getJSONObject(i);
                    Maneuver m = new Maneuver(resolveManeuverType(maneuver.getString("action")), maneuver.getInt("length"));
                    g.addManeuver(m);
                    
                    if (i==0) {
                        g.setNextInstr(maneuver.getString("instruction"));
                    }
                }
            }            
        }
    }
    
    static Maneuver.ManeuverType resolveManeuverType(String action) {
        
        switch(action) {
            case "depart": return Maneuver.ManeuverType.DEPART;
            case "arrive": return Maneuver.ManeuverType.ARRIVE;
            case "continue": return Maneuver.ManeuverType.STRAIGHT;
            case "leftExit": return Maneuver.ManeuverType.EXIT_LEFT;
            case "rightExit": return Maneuver.ManeuverType.EXIT_RIGHT;
            case "leftRamp": return Maneuver.ManeuverType.RAMP_LEFT;
            case "rightRamp": return Maneuver.ManeuverType.RAMP_RIGHT;
            case "leftTurn": return Maneuver.ManeuverType.TURN_LEFT;
            case "rightTurn": return Maneuver.ManeuverType.TURN_RIGHT;
            case "leftFork": return Maneuver.ManeuverType.FORK_LEFT;
            case "rightFork": return Maneuver.ManeuverType.FORK_RIGHT;
            case "rightRoundaboutExit1": return Maneuver.ManeuverType.RAB_1;
            case "rightRoundaboutExit2": return Maneuver.ManeuverType.RAB_2;
            case "rightRoundaboutExit3": return Maneuver.ManeuverType.RAB_3;
            case "rightRoundaboutExit4": return Maneuver.ManeuverType.RAB_4;
        }
        
        return Maneuver.ManeuverType.UNKNOWN;
    }
    
}
