/*
 * (c) 2016 Novetta
 */
package com.github.mike10004.mysqldistuser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mike
 */
public class DatabaseClient {
    
    public final String jdbcUrl, user, password;

    public DatabaseClient(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.user = user;
        this.password = password;
    }
    
    public Object doSomething() throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE widget (widget_id INTEGER AUTO_INCREMENT PRIMARY KEY, color VARCHAR(63) NOT NULL)");
            stmt.executeUpdate("INSERT INTO widget (color) VALUES ('red'), ('blue'), ('green'), ('purple'), ('yellow'), ('orange'), ('pink')");
            try (ResultSet rs = stmt.executeQuery("SELECT color FROM widget WHERE 1")) {
                List<String> colors = new ArrayList<>();
                while (rs.next()) {
                    colors.add(rs.getString(1));
                }
                return colors;
            }
        }
    }
        
}
