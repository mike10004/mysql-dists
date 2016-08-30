/*
 * (c) 2016 Novetta
 */
package com.github.mike10004.mysqldistuser;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author mike
 */
public class DatabaseClientIT {

    @Test
    public void doSomething() throws Exception {
        String jdbcUrl = "jdbc:mysql://localhost:" + resolvePort() + "/test";
        DatabaseClient client = new DatabaseClient(jdbcUrl, "root", "root");
        Object result = client.doSomething();
        System.out.println("result: " + result);
        assertNotNull("result is null", result);
    }

    private static int resolvePort() {
        return Integer.parseInt(System.getProperty("mysql.port"));
    }
}
