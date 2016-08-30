# mysql-dists

Projects that build mysql distribution artifacts for use as embedded
databases in Maven projects. These are designed to be compatible with 
[jcabi-mysql-maven-plugin](http://mysql.jcabi.com/).

Each subdirectory is an independent project configured to do the 
following:

* download a MySQL or MariaDB tarball 
* extract the tarball
* repackage the contents as a zip usable as a jcabi-mysql-maven-plugin
  MySQL distribution artifact

Each project is configured with a "practice" profile that tests the 
construction of that zip by downloading a tarball from the user web
directory on localhost. If you have Apache installed, you can enable 
the userdir module and put the `practice/ziptest-1.0.tar.gz` tarball
in your shared directory such that it is downloadable from the URL
**http://localhost/~USERNAME/mysql-dists-test/ziptest-1.0.tar.gz**.

To create a new MySQL distribution, make a copy of the `template` 
directory and change the `pom.xml` to reflect the correct filenames
and URLs of the MySQL distribution. Run `mvn install` in your new 
directory to the new MySQL distribution zip to your local repository.

To confirm that it works, in the `example` directory, run

    mvn verify -Dmysql-dist.artifactId=MY_DIST_ARTIFACTID -Dmysql-dist.version=MY_DIST_VERSION
    
and the project's integration tests will run using the embedded MySQL
distribution that you specified with the `mysql-dist.artifactId` and
`mysql-dist.version` system properties.
