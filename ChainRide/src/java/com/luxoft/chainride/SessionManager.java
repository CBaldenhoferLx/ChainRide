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

/**
 *
 * @author CBaldenhofer
 */
public class SessionManager {
    
    private static Connection c;

    public SessionManager() {
    }
    
    public static void init() throws SQLException, ClassNotFoundException {
        
        Class.forName("org.sqlite.JDBC");
        //c = DriverManager.getConnection("jdbc:sqlite::memory:");
        c = DriverManager.getConnection("jdbc:sqlite:c:\\temp\\sessions.db");
        
        Statement s = c.createStatement();
        
        s.addBatch("CREATE TABLE IF NOT EXISTS `users` ( `id` TEXT NOT NULL UNIQUE, `leaderId` TEXT, `lat` REAL, `lng` REAL, `lastPing` INTEGER, PRIMARY KEY(`id`) )");
        
        s.executeBatch();
        
        s.close();
        
        System.out.println("SessionManager init");
    }
    
    public static boolean maintainTables() throws SQLException {
        final long LASTPING_TIMEOUT = 30*1000;
        
        PreparedStatement s = c.prepareStatement("DELETE FROM users WHERE lastPing<?");
        s.setLong(1, System.currentTimeMillis() - LASTPING_TIMEOUT);
        return s.execute();
    }
    
    private static boolean updateUser(String id, String leaderId, double lat, double lng) throws SQLException {
        PreparedStatement s = c.prepareStatement("REPLACE INTO users (id, leaderId, lat, lng, lastPing) VALUES (?, ?, ?, ?, ?)");
        
        s.setString(1, id);
        s.setString(2, leaderId);
        s.setDouble(3, lat);
        s.setDouble(4, lng);
        s.setLong(5, System.currentTimeMillis());
        
        boolean returnVal = s.execute();
        
        s.close();        
        
        return returnVal;
    }
    
    public static boolean updateLeader(String id, double lat, double lng) throws SQLException {
        return updateUser(id, null, lat, lng);        
    }

    public static boolean updateFollower(String id, String leaderId, double lat, double lng) throws SQLException {
        return updateUser(id, leaderId, lat, lng);        
    }
    
    public static List<Leader> listLeaders() throws SQLException {
        List<Leader> returnList = new ArrayList<>();
        
        PreparedStatement s = c.prepareStatement("SELECT * FROM users WHERE leaderId IS NULL");
        ResultSet res = s.executeQuery();
        
        while(res.next()) {
            returnList.add(new Leader(Coordinates.getCoordinates(res.getDouble("lat"), res.getDouble("lng")), res.getString("id"), res.getLong("lastPing")));
        }
        
        res.close();
        s.close();
        
        return returnList;
    }
    
    public static Leader getLeader(String leaderId) throws SQLException {
        PreparedStatement s = c.prepareStatement("SELECT * FROM users WHERE leaderId=?");
        s.setString(1, leaderId);
        
        ResultSet res = s.executeQuery();
        if (res.next()) {
            return new Leader(Coordinates.getCoordinates(res.getDouble("lat"), res.getDouble("lng")), res.getString("id"), res.getLong("lastPing"));
        } else {
            return null;
        }
    }
    
    public static List<Follower> listFollowers(String leaderId) throws SQLException {
        List<Follower> returnList = new ArrayList<>();
        
        PreparedStatement s = c.prepareStatement("SELECT * FROM users WHERE leaderId=?");
        s.setString(1, leaderId);
        
        ResultSet res = s.executeQuery();
        
        while(res.next()) {
            returnList.add(new Follower(res.getString("id"), new Coordinates(res.getDouble("lat"), res.getDouble("lng"))));
        }
        
        res.close();
        s.close();
        
        return returnList;
    }
    
}
