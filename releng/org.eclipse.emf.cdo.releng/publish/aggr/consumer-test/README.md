# Plain-Java CDO Maven consumer test

This is a standalone Maven project that consumes the locally generated
`repository/final` repository. The twelve CDO/Net4j artifact IDs are stable,
but their versions are resolved from the selected repository after each
aggregation. `com.h2database:h2` remains an explicit, consumer-owned JDBC
driver dependency.

## Prerequisites and execution

Run `clean-build` in the parent aggregation first, then use the portable Ant
consumer target. It writes the generated properties to
`<work.dir>/consumer-test/cdo-consumer-versions.properties` and passes every
property as an individual Maven `-D` argument before invoking Maven:

```text
ant -Dcdo.drop.dir="C:\path\to\drop" -Dwork.dir="C:\path\to\work" -Dmaven.executable=mvn -f ../build.xml consumer-test
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
`maven.local.repository` is the read-only source cache used by Javadoc, while
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

The generated properties file also records `cdo.build.drop`, `cdo.git.commit`,
and `cdo.eclipse.simrel`; release values are not hard-coded in this project.

The tests instantiate client/common APIs (`ManagedContainer` and `CDOUtil`)
and server/H2 APIs (`H2Adapter` and H2's `JdbcDataSource`). This is deliberately
a smoke test, not a full embedded CDO repository/server integration test.

The aggregation also produces matching non-empty `-sources.jar` and
`-javadoc.jar` classifiers for all twelve CDO/Net4j artifacts. It also creates
`.md5`, `.sha1`, `.sha256`, and `.sha512` sidecars for each classifier and
validates all 96 values before the consumer phase. PGP signatures and Maven
Central publication remain separate release steps. The dependency
tree should show EMF artifacts under their published Maven coordinates and H2 only
through this POM's explicit dependency; no CDO/Net4j dependency is expected to
introduce the JDBC driver transitively. The fixed test prerequisites are H2
`2.4.240` and JUnit `4.13.2`; CDO/Net4j versions are never fixed in the POM.
