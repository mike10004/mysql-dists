/*
 * (c) 2016 Novetta
 */
package com.github.mike10004.mysqldistuser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author mike
 */
public class DatabaseClientIT {

    @Test
    public void doSomething() throws Exception {
        String jdbcUrl = "jdbc:mysql://localhost:" + resolvePort() + "/";
        String user = "root", password = "root";
        String databaseName = "DatabaseClientIT";
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
                Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE IF NOT EXISTS " + databaseName);
        }
        jdbcUrl = jdbcUrl + databaseName;
        DatabaseClient client = new DatabaseClient(jdbcUrl, "root", "root");
        Object result = client.doSomething();
        System.out.println("result: " + result);
        assertNotNull("result is null", result);
    }

    private static int resolvePort() {
        return Integer.parseInt(System.getProperty("mysql.port"));
    }
}
