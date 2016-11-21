/*
 * (c) 2016 Novetta
 *
 * Created by mike
 */
package com.github.mike10004.mysqldiststests;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.junit.BeforeClass;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DistsTest {

    private static @Nullable File mavenHome;

    private static void dumpMap(Map<String, String> map, PrintStream out) throws IOException {
        for (String key : Ordering.<String>natural().immutableSortedCopy(map.keySet())) {
            String value = StringEscapeUtils.escapeJava(System.getProperty(key));
            out.format("%s=%s%n", key, value);
        }
    }

    @BeforeClass
    public static void setMavenHome() throws IOException {
        System.out.println("system properties:");
        dumpMap(Maps.fromProperties(System.getProperties()), System.out);
        System.out.println("environment:");
        dumpMap(System.getenv(), System.out);
        mavenHome = resolveMavenHome();
    }

    private static File resolveMavenHome() throws IOException {
        final File mavenHome;
        String mavenHomeSystemProp = System.getProperty("maven.home");
        if (mavenHomeSystemProp != null) {
            mavenHome = new File(mavenHomeSystemProp);
            if (!mavenHome.isDirectory()) {
                throw new FileNotFoundException("maven.home directory not found " + mavenHome);
            }
            return mavenHome;
        } else {
            Iterable<String> suffixes = Arrays.asList("", ".bat", ".exe", ".com");
            Iterable<String> pathDirs = Splitter.on(System.getProperty("path.separator")).split(System.getenv("PATH"));
            for (String pathDir : pathDirs) {
                for (String suffix : suffixes) {
                    File mvnExecutable = new File(pathDir, "mvn" + suffix);
                    if (mvnExecutable.isFile() && mvnExecutable.canExecute()) {
                        mvnExecutable = mvnExecutable.getCanonicalFile();
                        Path bin = mvnExecutable.toPath().getParent();
                        if (bin == null) {
                            throw new FileNotFoundException("could not resolve maven home from executable " + mvnExecutable);
                        }
                        Path home = bin.getParent();
                        if (home == null) {
                            throw new FileNotFoundException("could not resolve maven home from executable " + mvnExecutable);
                        }
                        mavenHome = home.toFile();
                        if (mavenHome.isDirectory()) {
                            return mavenHome;
                        } else {
                            throw new FileNotFoundException("maven home is not a directory: " + mavenHome);
                        }
                    }
                }
            }
        }
        throw new FileNotFoundException("could not resolve maven home; please set maven.home system property");
    }

    @org.junit.Test
    public void test_mysql_5_7_14() throws Exception {
        new DistTester("mysql-dist", "5.7.14", mavenHome).perform();
    }

    @org.junit.Test
    public void test_mariadb_10_0_27() throws Exception {
        new DistTester("mariadb-dist", "10.0.27", mavenHome).perform();
    }

    @org.junit.Test
    public void test_mariadb_10_1_17() throws Exception {
        new DistTester("mariadb-dist", "10.1.17", mavenHome).perform();
    }

    private static class DistTester {

        private final String artifactId;
        private final String version;
        private final @Nullable File mavenHome;

        public DistTester(String artifactId, String version, @Nullable File mavenHome) {
            this.artifactId = artifactId;
            this.version = version;
            this.mavenHome = mavenHome;
        }

        public void perform() throws IOException, MavenInvocationException {
            Path basedir = resolveExampleDirectory();
            InvocationRequest invocationRequest = new DefaultInvocationRequest();
            invocationRequest.setBaseDirectory(basedir.toFile());
            invocationRequest.setGoals(Collections.singletonList("verify"));
            Properties properties = buildProperties();
            invocationRequest.setProperties(properties);
            Invoker invoker = new DefaultInvoker();
            if (mavenHome != null) {
                invoker.setMavenHome(mavenHome);
            }
            InvocationResult result = invoker.execute(invocationRequest);
            @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
            CommandLineException exception = result.getExecutionException();
            if (exception != null) {
                exception.printStackTrace(System.out);
                fail("command line exception present: " + exception);
            }
            assertEquals("exit code", 0, result.getExitCode());
        }

        protected Properties buildProperties() {
            Properties properties = new Properties();
            properties.setProperty("mysql-dist.artifactId", artifactId);
            properties.setProperty("mysql-dist.version", version);
            return properties;
        }

        protected Path resolveExampleDirectory() throws IOException {
            Path parent = resolveParentProjectBaseDir();
            Path dir = parent.resolve("example");
            if (!dir.toFile().isDirectory()) {
                throw new FileNotFoundException("project directory " + dir);
            }
            return dir;
        }

        protected Path resolveParentProjectBaseDir() throws IOException {
            Properties p = new Properties();
            try (InputStream in = getClass().getResourceAsStream("/mysql-dists-tests/maven.properties")) {
                p.load(in);
            }
            String path = p.getProperty("project.parent.basedir");
            if (path == null) {
                throw new IOException("project.parent.basedir is not set");
            }
            File f = new File(path);
            if (!f.isDirectory()) {
                throw new FileNotFoundException("not a directory: " + f);
            }
            return f.toPath();
        }

    }
}
