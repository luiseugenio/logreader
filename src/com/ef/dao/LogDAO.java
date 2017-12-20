/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ef.dao;

import com.ef.models.Arguments;
import com.ef.models.Log;
import com.ef.services.Utilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author luis
 */
public class LogDAO {

    private Connection connection;

    public LogDAO() {
        cleanDatabase();
    }

    private void cleanDatabase() {
        String sql = "DROP TABLE lp_logs;";

        try {
            connection = ConnectionMySQL.getConnectionMySQL();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE lp_logs (\n"
                    + "    id INT NOT NULL AUTO_INCREMENT,\n"
                    + "    date timestamp,\n"
                    + "    ip varchar(255),\n"
                    + "    request varchar(255),\n"
                    + "    status varchar(255),\n"
                    + "    user_agent varchar(255),\n"
                    + "    PRIMARY KEY (id)\n"
                    + ");";
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(LogDAO.class.getName()).log(Level.CONFIG, null, ex);
            }
        }
    }

    public void insert(List<Log> logs) {
        String sql = "INSERT INTO lp_logs(date, ip, request, status, user_agent) VALUES (?,?,?,?,?)";
        try {
            connection = ConnectionMySQL.getConnectionMySQL();
            PreparedStatement stmt = connection.prepareStatement(sql);
            logs.forEach((Log log) -> {
                try {
                    stmt.setTimestamp(1, Utilities.convertStringToTimestamp(log.getDate()));
                    stmt.setString(2, log.getIp());
                    stmt.setString(3, log.getRequest());
                    stmt.setString(4, log.getStatus());
                    stmt.setString(5, log.getUserAgent());
                    stmt.addBatch();
                } catch (ParseException | SQLException ex) {
                    Logger.getLogger(LogDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(LogDAO.class.getName()).log(Level.CONFIG, null, ex);
            }
        }
    }

    public List<Log> getLogsBy(Arguments args) {
        List<Log> logs = new ArrayList<>();
        try {
            connection = ConnectionMySQL.getConnectionMySQL();
            String sql = "SELECT *, COUNT(*)\n"
                    + "FROM lp_logs\n"
                    + "WHERE  date >= ? AND date <=  DATE_ADD(?, INTERVAL 1 HOUR)\n"
                    + "GROUP BY ip\n"
                    + "HAVING COUNT(*) >= ?\n"
                    + "ORDER BY COUNT(*)";
            if (args.getDuration().equalsIgnoreCase("daily")) {
                sql = sql.replace("HOUR", "DAY");
            }
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setTimestamp(1, args.getStartDate());
            stmt.setTimestamp(2, args.getStartDate());
            stmt.setInt(3, args.getThreshold());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Log log = new Log(Utilities.convertTimestampToString(rs.getTimestamp("date")), rs.getString("ip"), rs.getString("request"), rs.getString("status"), rs.getString("user_agent"));
                logs.add(log);
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(LogDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(LogDAO.class.getName()).log(Level.CONFIG, null, ex);
            }
        }

        return logs;
    }

}
