# Plain-Java CDO Maven consumer test

This is a standalone Maven project that consumes the locally generated
`repository/final` repository. The 21 CDO/Net4j artifact IDs are stable,
but their versions are resolved from the selected repository after each
aggregation. `com.h2database:h2` remains an explicit, consumer-owned JDBC
driver dependencies. The PostgreSQL adapter keeps its driver imports optional;
the consumer adds `org.postgresql:postgresql` explicitly at runtime. Version
`42.7.13` is the locally tested example from `CDO-Maven.target`, not a version
requirement imposed by the adapter. Consumers should select a driver release
compatible with the adapter's OSGi range `[42.7.0,43.0.0)`.

## Prerequisites and execution

Run `clean-build` in the parent aggregation first, then use the portable Ant
consumer target. It writes the generated properties to
`<work.dir>/consumer-test/cdo-consumer-versions.properties` and passes every
property as an individual Maven `-D` argument before invoking Maven:

```text
ant -Dcdo.drop.location="C:\path\to\drop" -Dwork.dir="C:\path\to\work" -Dmaven.executable=mvn -f ../build.xml consumer-test
```

The Maven executable defaults to `mvn` on `PATH`; set `-Dmaven.executable` to
another executable or absolute path when necessary. The repository defaults
to `<work.dir>/repository/final`. An alternative aggregation result can be
selected with `-Dcdo.maven.repository`:

```text
ant -Dcdo.maven.repository=C:/path/to/repository/final -Dwork.dir=C:/path/to/work -f ../build.xml consumer-test
ant -Dcdo.maven.repository=C:/path/to/repository/final -Dwork.dir=C:/path/to/work -f ../build.xml consumer-dependency-tree
```

The two Maven properties intentionally have different semantics:
`maven.local.repository` is the read-only source cache, while
`maven.consumer.repository` is the isolated Maven cache used by the consumer and
dependency-tree invocations. Before Maven starts, Ant copies the former into
the latter below `work.dir`. This avoids stale or locked global JARs on
Windows. Both properties can be overridden independently.

Advanced users can run Maven directly, but must pass the generated version
properties as system properties because Maven evaluates dependency versions
before it can load an arbitrary external properties file:

```text
mvn -Dcdo.maven.repository=C:/path/to/repository/final \
    -Dcdo.version.org.eclipse.net4j.util=... \
    -Dcdo.version.org.eclipse.emf.cdo=... clean test
```

The generated properties file also records `cdo.drop.id`, `cdo.git.commit`,
and `cdo.eclipse.simrel`; release values are not hard-coded in this project.

The tests instantiate client/common APIs (`ManagedContainer` and `CDOUtil`),
server/H2 APIs (`H2Adapter` and H2's `JdbcDataSource`), and load the explicitly
declared PostgreSQL driver. Focused consumer integration tests also exercise JVM
and Jetty-backed WebSocket request/response, an embedded in-memory CDO
repository, representative Expressions and Security model APIs, and a
read-only authenticated CDO session whose attempted write is rejected. These
tests do not require a PostgreSQL service. The consumer project compiles for
Java 17, matching the Jetty 12.1 runtime required by the WebSocket server
bundle. These tests resolve CDO/Net4j from the selected consumer repository;
they can be run against Maven Central after the expanded release is published.

The CBI aggregation produces the `-sources.jar` and README-based
`-javadoc.jar` classifiers for all 21 CDO/Net4j artifacts. The publishing
workflow validates these native classifiers and preserves correct existing
`.md5`, `.sha1`, `.sha256`, and `.sha512` sidecars, filling only missing values.
The readiness audit validates all 168 classifier checksums (42 classifier JARs
with four checksum sidecars each). PGP signatures and
Maven Central publication remain separate release steps. The dependency
tree should show EMF artifacts under their published Maven coordinates and H2 and
PostgreSQL only through this POM's explicit driver dependencies; no CDO/Net4j
dependency should introduce either JDBC driver transitively. The fixed test
prerequisites are H2 `2.4.240`, PostgreSQL `42.7.13`, and JUnit `4.13.2`;
CDO/Net4j versions are never fixed in the POM.
