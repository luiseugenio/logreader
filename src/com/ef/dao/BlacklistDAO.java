/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.dao;

import com.ef.models.Blacklist;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luis
 */
public class BlacklistDAO {

    private Connection connection;

    public void insert(Blacklist logged) {
        String sql = "INSERT INTO lp_blacklist(ip, reason) VALUES (?,?)";
        try {
            connection = ConnectionMySQL.getConnectionMySQL();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, logged.getIp());
            stmt.setString(2, logged.getReason());
            stmt.execute();
        } catch (SQLException e) {
            if (!e.getMessage().contains("MySQLIntegrityConstraintViolationException")) {
                throw new RuntimeException(e);
            }
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(BlacklistDAO.class.getName()).log(Level.CONFIG, null, ex);
            }
        }
    }
}
