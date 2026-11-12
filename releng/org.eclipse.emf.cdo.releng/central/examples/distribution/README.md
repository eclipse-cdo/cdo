# CDO Plain Java Examples

Requires JDK 17 and Maven 3.9+. Build all examples from this directory with `mvn clean package`.

## Embedded H2 Example
 
Run the embedded H2 example with `mvn -pl embedded-h2 exec:java -Dexec.mainClass=org.eclipse.emf.cdo.examples.plain.embedded.EmbeddedH2Example`. 

## Remote Server Example
 
Run the server with `mvn -pl remote-server exec:java -Dexec.mainClass=org.eclipse.emf.cdo.examples.plain.server.RemoteServer`.

RemoteServer requires PostgreSQL. Create a database and user, grant the user access to that database, then set `CDO_DB_URL` (for example `jdbc:postgresql://localhost:5432/cdo`), `CDO_DB_USER`, and optionally `CDO_DB_PASSWORD` before starting it.

The server listens on `0.0.0.0:2036` by default. Its optional argument is a port, bind address, or `bind-address:port`.

## Remote Client Example
 
Run the client with `mvn -pl remote-client exec:java -Dexec.mainClass=org.eclipse.emf.cdo.examples.plain.client.RemoteClient`.

RemoteClient connects to `localhost:2036` by default; its optional argument is a port, hostname, or `hostname:port`.

## Dependencies

CDO and Net4j core dependencies use the CDO 4.36.0 BOM in the root POM, which keeps the independently versioned Maven artifacts aligned. The examples also use the company model, embedded-server, PostgreSQL-adapter, and Net4j utility artifacts at the versions declared in the POMs. Those artifacts must be available from Maven Central alongside the ZIP. Maven downloads dependencies into its normal local cache; the ZIP does not include dependency JARs.
