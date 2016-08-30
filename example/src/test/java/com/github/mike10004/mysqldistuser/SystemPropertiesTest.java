/*
 * (c) 2016 Novetta
 */
package com.github.mike10004.mysqldistuser;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mike
 */
public class SystemPropertiesTest {
    
    @Test
    public void confirmPropertiesAreSet() {
        String distArtifactId = System.getProperty("mysql-dist.artifactId");
        String distVersion = System.getProperty("mysql-dist.version");
        System.out.format("using mysql-dist %s-%s%n", distArtifactId, distVersion);
        String messageSuffix = " not defined; specify profile or system properties mysql-dist.artifactId and mysql-dist.version";
        assertNotNull("dist artifactId " + messageSuffix, distArtifactId);
        assertNotNull("dist version " + messageSuffix, distVersion);
    }
    
}
