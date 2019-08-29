/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.luxoft.chainride;

import com.luxoft.chainride.model.Coordinates;
import com.luxoft.chainride.model.Follower;
import com.luxoft.chainride.model.Leader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author CBaldenhofer
 */
public class SessionManager {
    
    private static Connection c;
    
    private static boolean isInitialized = false;
    
    private static final Object syncObj = new Object();

    public SessionManager() {
    }
    
    public static void init() throws SQLException, ClassNotFoundException {
        if (isInitialized) {
            c.close();
        }
        
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite::memory:");
        //c = DriverManager.getConnection("jdbc:sqlite:c:\\temp\\sessions.db");
        
        synchronized(syncObj) {
            Statement s = c.createStatement();

            s.addBatch("CREATE TABLE IF NOT EXISTS `users` ( `id` TEXT NOT NULL UNIQUE, `leaderId` TEXT, `lat` REAL, `lng` REAL, `lastPing` INTEGER, `lastGuidanceLat` REAL, `lastGuidanceLng` REAL, PRIMARY KEY(`id`) )");

            s.executeBatch();

            s.close();
        }
        
        System.out.println("SessionManager init");
        
        isInitialized = true;

        Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
            try {
                System.out.println("Shutdown");
                c.close();
            } catch (SQLException ex) {
                Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
    }
    
    private static boolean executeSql(PreparedStatement s) {
        boolean returnVal = false;

        synchronized(syncObj) {
            try {
                returnVal = s.execute();
                s.close();
            } catch (SQLException ex) {
                Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return returnVal;
    }
    
    private static ResultSet executeQuery(PreparedStatement s) {
        ResultSet res = null;
        
        synchronized(syncObj) {
            try {
                res = s.executeQuery();
            } catch (SQLException ex) {
                Logger.getLogger(SessionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return res;
    }
    
    public static boolean maintainTables() throws SQLException {
        final long LASTPING_TIMEOUT = 30*1000;

        final PreparedStatement s = c.prepareStatement("DELETE FROM users WHERE lastPing<?");
        s.setLong(1, System.currentTimeMillis() - LASTPING_TIMEOUT);
        return executeSql(s);
    }
    
    private static boolean updateUser(String id, String leaderId, double lat, double lng) throws SQLException {
        final PreparedStatement s0 = c.prepareStatement("SELECT lastGuidanceLat, lastGuidanceLng FROM users WHERE id=?");
        s0.setString(1, id);
        
        final ResultSet res0 = executeQuery(s0);
        
        Double lastGuidanceLat = null;
        Double lastGuidanceLng = null;
        
        if (res0.next()) {
            lastGuidanceLat = res0.getDouble("lastGuidanceLat");
            lastGuidanceLng = res0.getDouble("lastGuidanceLng");
            res0.close();
            
            if (lastGuidanceLat==0 && lastGuidanceLng==0) {
                lastGuidanceLat = null;
                lastGuidanceLng = null;
            }
        }
        
        final PreparedStatement s = c.prepareStatement("REPLACE INTO users (id, leaderId, lat, lng, lastPing, lastGuidanceLat, lastGuidanceLng) VALUES (?, ?, ?, ?, ?, ?, ?)");

        s.setString(1, id);
        if (leaderId!=null) {
            s.setString(2, leaderId);
        } else {
            s.setNull(2, java.sql.Types.VARCHAR);
        }
        s.setDouble(3, lat);
        s.setDouble(4, lng);
        s.setLong(5, System.currentTimeMillis());
        if (lastGuidanceLat!=null) {
            s.setDouble(6, lastGuidanceLat);
        } else {
            s.setNull(6, java.sql.Types.DOUBLE);
        }
        if (lastGuidanceLng!=null) {
            s.setDouble(7, lastGuidanceLng);
        } else {
            s.setNull(7, java.sql.Types.DOUBLE);
        }

        return executeSql(s);        
    }
    
    public static boolean updateLeader(String id, double lat, double lng) throws SQLException {
        return updateUser(id, null, lat, lng);        
    }

    public static boolean updateFollower(String id, String leaderId, double lat, double lng) throws SQLException {
        return updateUser(id, leaderId, lat, lng);        
    }
    
    public static List<Leader> listLeaders() throws SQLException {
        List<Leader> returnList = new ArrayList<>();
        
        final PreparedStatement s = c.prepareStatement("SELECT * FROM users WHERE leaderId IS NULL");
        final ResultSet res = executeQuery(s);
        
        if (res!=null) {
            while(res.next()) {
                returnList.add(new Leader(Coordinates.getCoordinates(res.getDouble("lat"), res.getDouble("lng")), res.getString("id"), res.getLong("lastPing")));
            }

            res.close();
        }
        
        return returnList;
    }
    
    public static Leader getLeader(String id) throws SQLException {
        Leader returnVal = null;
        
        PreparedStatement s = c.prepareStatement("SELECT * FROM users WHERE id=?");
        s.setString(1, id);
        
        ResultSet res = executeQuery(s);
        if (res!=null) {
            if (res.next()) {
                returnVal = new Leader(Coordinates.getCoordinates(res.getDouble("lat"), res.getDouble("lng")), res.getString("id"), res.getLong("lastPing"));
            }
        }
        
        return returnVal;
    }
    
    public static List<Follower> listFollowers(String leaderId) throws SQLException {
        final List<Follower> returnList = new ArrayList<>();
        
        PreparedStatement s = c.prepareStatement("SELECT * FROM users WHERE leaderId=?");
        s.setString(1, leaderId);

        ResultSet res = executeQuery(s);

        if (res!=null) {
            while(res.next()) {
                returnList.add(new Follower(res.getString("id"), new Coordinates(res.getDouble("lat"), res.getDouble("lng"))));
            }

            res.close();
        }
        
        return returnList;
    }
    
    public static Coordinates getLastGuidance(String id) throws SQLException {
        Coordinates returnVal = null;
        
        PreparedStatement s = c.prepareStatement("SELECT lastGuidanceLat, lastGuidanceLng FROM users WHERE id=? AND lastGuidanceLat IS NOT NULL AND lastGuidanceLng IS NOT NULL");
        s.setString(1, id);

        ResultSet res = executeQuery(s);

        if (res!=null) {
            if (res.next()) {
                returnVal = new Coordinates(res.getDouble("lastGuidanceLat"), res.getDouble("lastGuidanceLng"));
            }
        }
        
        return returnVal;
    }
    
    public static boolean updateLastGuidance(String id, Coordinates coord) throws SQLException {
        final PreparedStatement s = c.prepareStatement("UPDATE users SET lastGuidanceLat=?, lastGuidanceLng=? WHERE id=?");
        s.setDouble(1, coord.getLat());
        s.setDouble(2, coord.getLng());
        s.setString(3, id);

        return executeSql(s);
    }
    
}
