# Plain Java embedded H2 example

This Java 17 application starts an embedded CDO repository backed by a persistent file-based H2 database. It registers the existing Company model, creates the Company resource on first use, and loads it on later runs through a read-only CDO view. Maven runs the same application in two separate JVMs to demonstrate persistence across process boundaries.

See also [CDO Programmer's Guide](https://download.eclipse.org/modeling/emf/cdo/updates/integration/help/?topic=org.eclipse.emf.cdo.doc/html/programmers/Doc01_Introduction.html).

## Prerequisites and dependencies

Install a JDK 17 and Maven 3.9 or later. The project is an ordinary Maven build; it does not need Eclipse, PDE, or an OSGi framework.

CDO and Net4j artifacts are resolved from the prepared Maven repository produced by the Central aggregation, with Maven Central used for the external EMF, H2, and plugin dependencies. The default prepared repository is `../../target/central/final` relative to `central/examples`. To use another prepared repository, pass its path as `-Dcdo.maven.repository=...`. The CDO artifact versions are independent; override the corresponding `cdo.*.version` properties when using a prepared repository from a different CDO drop.

The Company model artifact is included in the aggregation inventory by `CDO.aggr`. If it is not yet in the prepared repository, generate the Central repository with the existing workflow, for example:

```powershell
ant -Dcdo.drop.location="C:\path\to\promoted\CDO-drop" -f releng/org.eclipse.emf.cdo.releng/central/build.xml clean-build
```

## Run the application manually

From the repository root, run:

```powershell
mvn -f releng/org.eclipse.emf.cdo.releng/central/examples/embedded-h2/pom.xml compile exec:exec -Dexec.application.args=target/manual-db
```

The Maven POM supplies the Java executable, runtime classpath, and main class. The database directory defaults to `target/embedded-h2-data` when no argument is supplied. Pass a different directory through `exec.application.args` to keep multiple manual repositories separate. Existing data is preserved across runs. Maven build output is isolated in `target/build`, so `mvn clean` does not remove the manual database. To start fresh, stop the application and remove that database directory yourself.

For an application argument containing spaces, pass the complete Maven property as one PowerShell argument. The Exec Plugin parser keeps the quoted value as one application argument:

```powershell
mvn -f releng/org.eclipse.emf.cdo.releng/central/examples/embedded-h2/pom.xml compile exec:exec '-Dexec.application.args=target/my database'
```

Embedded quotation marks and other shell-sensitive characters may need shell-specific escaping. Prefer ordinary argument values when possible.

## Run the two-JVM verification

From the repository root:

```powershell
mvn -f releng/org.eclipse.emf.cdo.releng/central/examples/pom.xml clean verify
```

Maven removes only `embedded-h2/target/build/verification-db`, then launches the application twice as forked Java processes. It checks that the first process exits successfully and prints `CREATED`, then checks that a second process exits successfully and prints `LOADED` from the same database, including the expected Company, Category, and Product names. The verification database and all Maven outputs remain under the child project's `target` directory. Running `mvn verify` again without `clean` also resets only this isolated verification database.
