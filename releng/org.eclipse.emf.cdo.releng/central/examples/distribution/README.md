# CDO Plain Java Examples

Requires JDK 17 and Maven 3.9+. Build all examples from this directory with `mvn clean verify`. The `verify` phase compiles all four modules and runs the embedded H2 persistence check in two separate JVMs. The remote and configured servers and client are compiled; database-backed remote servers are not started.

## Embedded H2 Example
 
Run the embedded H2 example with `mvn -pl embedded-h2 exec:java -Dexec.mainClass=org.eclipse.emf.cdo.examples.plain.embedded.EmbeddedH2Example`. 

## Remote Server Example
 
Run the server with `mvn -pl remote-server exec:java -Dexec.mainClass=org.eclipse.emf.cdo.examples.plain.server.RemoteServer`.

RemoteServer requires PostgreSQL. Create a database and user, grant the user access to that database, then set `CDO_DB_URL` (for example `jdbc:postgresql://localhost:5432/cdo`), `CDO_DB_USER`, and optionally `CDO_DB_PASSWORD` before starting it.

The server listens on `127.0.0.1:2036` by default, so it accepts local connections only. Its optional argument is a port, bind address, or `bind-address:port`. To allow remote connections, pass an explicit address such as `0.0.0.0:2036` and configure your network accordingly.

## Remote Client Example
 
Run the client with `mvn -pl remote-client exec:java -Dexec.mainClass=org.eclipse.emf.cdo.examples.plain.client.RemoteClient`.

RemoteClient connects to `localhost:2036` by default; its optional argument is a port, hostname, or `hostname:port`.

## Configured Server Example

The configured server runs the standard CDO server from `config/cdo-server.xml`. It supports H2 by default or PostgreSQL, and TCP, SSL, WS, and WSS transports. See `configured-server/README.md` for its settings and commands. Its WebSocket listener requires Jetty, which is declared by that module.

## Dependencies

All CDO and Net4j dependencies in the examples are managed by the project-release BOM imported by the root POM. The BOM version is normalized from the selected CDO project release, keeping independently versioned Maven artifacts aligned without per-example version declarations. Third-party dependencies and Maven plugins retain their own versions.

The distribution root POM is generated from the canonical `../pom.xml`; the declared Maven modules and their source-file properties determine the packaged module POMs and canonical Java sources. The generated root removes checkout-only plugin configuration and each packaged module POM omits checkout-dependent profiles. The examples distribution is independently versioned from the CDO project release and the bundles it consumes. `package.properties` sets its fixed major/minor line with `release.version.line=1.0`. During PACKAGE, the build compares the candidate ZIP and consumer-relevant POM semantics with the latest published patch on that line. It reuses an equivalent coordinate or selects the next numeric patch when content changes. The configured version line changes only by an explicit edit. The imported CDO BOM remains tied to the normalized project-release version, so a new BOM in the generated root POM changes the ZIP and advances the examples patch. The normal `PACKAGE` deployment ZIP contains the CDO/Net4j Maven artifacts, the project-release BOM, and this standalone examples distribution. Extract the complete deployment ZIP, then extract the examples ZIP from its own version directory. Run Maven from the extracted examples root and set `cdo.deployment.repository` to a `file:` URI for the extracted deployment repository. Use a fresh `maven.repo.local` for a clean local cache; Resolver provenance checks separately verify that resolved CDO artifact bytes came from the deployment repository. Maven plugins and third-party dependencies continue to resolve from Maven Central.

In Windows PowerShell:

```powershell
$repository = Join-Path $PWD 'deployment-repository'
Expand-Archive -LiteralPath .\cdo-maven-central-deployment.zip -DestinationPath $repository
$examplesArtifact = 'cdo-examples-distribution'
$examplesVersionDirectories = @(Get-ChildItem (Join-Path $repository "org\eclipse\cdo\$examplesArtifact") -Directory)
if ($examplesVersionDirectories.Count -ne 1) { throw 'Expected exactly one examples distribution version directory.' }
$examplesVersionDirectory = $examplesVersionDirectories[0]
$examplesVersion = $examplesVersionDirectory.Name
$examplesZip = Get-ChildItem (Join-Path $examplesVersionDirectory.FullName "$examplesArtifact-$examplesVersion-examples.zip")
$examples = Join-Path $PWD 'standalone-examples'
Expand-Archive -LiteralPath $examplesZip.FullName -DestinationPath $examples
$repositoryUri = ([System.Uri]::new((Resolve-Path $repository).Path)).AbsoluteUri
mvn "-Dmaven.repo.local=$(Join-Path $PWD 'maven-local')" "-Dcdo.deployment.repository=$repositoryUri" -B -ntp -f (Join-Path $examples 'pom.xml') clean verify
```

On a Unix-like Maven environment:

```sh
unzip -q cdo-maven-central-deployment.zip -d deployment-repository
examples_version=$(find deployment-repository/org/eclipse/cdo/cdo-examples-distribution -mindepth 1 -maxdepth 1 -type d -printf '%f\n')
unzip -q "deployment-repository/org/eclipse/cdo/cdo-examples-distribution/$examples_version/cdo-examples-distribution-$examples_version-examples.zip" -d standalone-examples
mvn -Dmaven.repo.local="$PWD/maven-local" -Dcdo.deployment.repository="file://$(realpath deployment-repository)" \
  -B -ntp -f standalone-examples/pom.xml clean verify
```

The BOM must be present at the exact release coordinate inside `deployment-repository`. The build does not rely on a matching or older BOM published to Maven Central.
