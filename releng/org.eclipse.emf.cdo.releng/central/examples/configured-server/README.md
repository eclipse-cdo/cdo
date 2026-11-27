# Configured CDO server

## 1. Overview

This Maven example runs the standard `org.eclipse.emf.cdo.server.Main` with the included
`config/cdo-server.xml`. The server starts repository `repo1`, whose DB store persists CDO data in
H2 by default or PostgreSQL when selected. The database is the repository's storage; TCP, SSL, WS,
and WSS are alternative transports for client/server communication.

The existing `RemoteClient` connects to `repo1` and uses the Company model. On the first run it
creates a `Company` named `Example Company`, a `Category` named `Hardware`, and two products:
`Keyboard` (49.95, VAT15) and `Mouse` (19.95, VAT15). It creates resource `/company` in a CDO
transaction and commits it. After that commit, it waits for its read-only view to receive the
update, prints `CREATED`, and reads and prints the Company.

On later runs, the client checks whether `/company` exists. If it does, the client prints `LOADED`
and reads the saved Company and its contents. The transaction commit stores the data in the
repository's database. With the default file-based H2 URL, the data therefore remains available
across separate client processes and server restarts.

## 2. Configuration

Database selection and transport selection are independent. The included
[server configuration](config/cdo-server.xml) uses Java system properties with defaults evaluated
by the existing `XMLConfiguration` converters. It defines database properties and the TCP/SSL
acceptor bindings.

### 2.1 Database

The repository uses a database for persistent storage. Since the XML omits `<dbAdapter>`, CDO
detects the appropriate DBAdapter from a temporary JDBC connection to the configured data source.

#### 2.1.1 H2 (default)

H2 is embedded and stores its database in a file. Its default URL uses the configured-server
module's absolute path so it is stable regardless of Maven's launch directory.

| Property | Default | Description |
|---|---|---|
| `db` | `h2` | Selects the database implementation. |
| `cdo.db.url` | configured-server `data/cdo` | JDBC URL for the H2 file database; override with `-Dcdo.db.url=...`. |

The module sets `net4j.config` to its `${project.basedir}/config`, so the same
`config/cdo-server.xml` is found whether Maven is launched from the examples root or this module
directory. The file
under `data/cdo` keeps repository data when the server stops and starts again. Do not
delete it if you want to retain the example data.

#### 2.1.2 PostgreSQL

Select the Maven `postgresql` profile and set `db=postgresql`. An installed, running PostgreSQL
server, an existing database, and a database user with suitable permissions are prerequisites. See
the official [PostgreSQL downloads](https://www.postgresql.org/download/) and
[`CREATE DATABASE` documentation](https://www.postgresql.org/docs/current/sql-createdatabase.html).
The PostgreSQL driver and CDO adapter are added by that Maven profile.

| Property | Default | Description |
|---|---|---|
| `db` | `h2` | Set to `postgresql` to select PostgreSQL. |
| `cdo.db.server` | `localhost` | PostgreSQL host name. |
| `cdo.db.port` | `5432` | PostgreSQL port. |
| `cdo.db.database` | `cdo` | Database name. |
| `cdo.db.user` | `cdo` | Database user. |
| `cdo.db.password` | No default | Password for the database user. |

These PostgreSQL connection properties are used only when PostgreSQL is selected.

### 2.2 Transport

The transport controls how the CDO client communicates with the server. It is independent of the
database selection. `transport` defaults to `tcp` when absent.

#### 2.2.1 TCP (default)

Use `transport=tcp`. The configured acceptor listens on `0.0.0.0:2036`, which binds all IPv4
interfaces. Other machines may be able to reach it depending on routing and firewall rules. The
client's default endpoint is `localhost:2036`.

#### 2.2.2 SSL

Use `transport=ssl`. The SSL acceptor also listens on `0.0.0.0:2036`. Net4j SSL uses the
`org.eclipse.net4j.tcp.ssl.*` system properties. The server keystore supplies the server
certificate and private key; the client truststore validates that certificate. The client must
trust the certificate, and certificate hostname verification and trust checks must not be bypassed.
See the Java 17 [`keytool`](https://docs.oracle.com/en/java/javase/17/docs/specs/man/keytool.html)
and [JSSE Reference Guide](https://docs.oracle.com/en/java/javase/17/security/java-secure-socket-extension-jsse-reference-guide.html).
The Maven POM maps these Maven properties to Net4j's SSL system properties:

| Maven property | Purpose |
|---|---|
| `cdo.ssl.keystore` | Server keystore path or `file:` URL. |
| `cdo.ssl.truststore` | Client truststore path or `file:` URL. |
| `cdo.ssl.passphrase` | Keystore/truststore passphrase used by Net4j SSL. |

The example includes no keys or trust stores. Keep those files outside the checkout and use a
trust store that validates the server certificate. Net4j SSL uses this example's Net4j-specific
property mechanism, separate from the JSSE mechanism used by WSS.

#### 2.2.3 WebSocket

Use `transport=ws`. `ConfiguredWebSocketServerExtension` starts the Jetty listener through the
standard CDO app-extension lifecycle. It binds `0.0.0.0:8080` by default; `cdo.ws.host` and
`cdo.ws.port` can override the bind host and port. The extension maps `/net4j/*` and exposes the
Net4j acceptor named `cdo`, so the endpoint is `ws://<host>:<port>/net4j/@cdo`. The included client
uses this route by default. Set client property `cdo.ws.path` to another service path when a server
uses a different mapping; `/` selects a root mapping. `0.0.0.0` listens on all IPv4 interfaces;
access still depends on routing and firewall rules. WS is not encrypted.

#### 2.2.4 Secure WebSocket

Use `transport=wss`. The same Jetty extension binds `0.0.0.0:8443` by default and uses the same
`/net4j/*` handler and `cdo` acceptor route, giving the server endpoint
`wss://<host>:<port>/net4j/@cdo`. The client's `cdo.ws.path` property can select another service
path. Configure a PKCS12 keystore outside the checkout:

| Maven property | Purpose |
|---|---|
| `cdo.wss.keystore` | Server keystore filesystem path. |
| `cdo.wss.keystore.password` | Server keystore password. |
| `cdo.wss.truststore` | Client truststore filesystem path. |
| `cdo.wss.truststore.password` | Client truststore password. |

The server keystore supplies the certificate and private key Jetty presents to clients. The client
truststore validates that certificate; the client must trust it, and certificate hostname
verification and trust checks must not be bypassed. This example configures Jetty WSS through
JSSE `javax.net.ssl.*` properties, separate from Net4j SSL's property mechanism. See Java 17
[`keytool`](https://docs.oracle.com/en/java/javase/17/docs/specs/man/keytool.html), the
[JSSE Reference Guide](https://docs.oracle.com/en/java/javase/17/security/java-secure-socket-extension-jsse-reference-guide.html),
and [Jetty 12.1 Keystore Configuration](https://jetty.org/docs/jetty/12.1/operations-guide/keystore/index.html).

### 2.3 Repository

The example keeps repository configuration small:

- `supportingAudits=false` and `supportingBranches=false` disable those repository features.
- `idGenerationLocation=STORE` assigns object IDs on the server.
- The `horizontal` mapping strategy stores model features in relational tables. It uses qualified
  names and the registered type-mapping provider; table creation is not eager.
- The Company model package is registered as an initial package so the client can persist its
  objects.

## 3. Running the Example

### 3.1 Build

Use JDK 17 or newer, Maven, and access to Maven Central. Run commands from
`releng/org.eclipse.emf.cdo.releng/central/examples`:

```powershell
mvn -pl configured-server,remote-client package
```

For PostgreSQL, build the server module with its profile enabled:

```powershell
mvn -pl configured-server -Ppostgresql package
```

The examples use the CDO BOM version declared in the root examples POM and published CDO
artifacts; Maven must be able to resolve those dependencies.

### 3.2 Start the Server

From the examples directory, start the server with default H2 storage and TCP transport:

```powershell
mvn -pl configured-server exec:exec
```

Override transport with `-Dtransport=ssl`, `-Dtransport=ws`, or
`-Dtransport=wss`. For PostgreSQL, use `-Ppostgresql` and provide any non-default
`cdo.db.server`, `cdo.db.port`, `cdo.db.database`, `cdo.db.user`, and `cdo.db.password` values as
Maven `-D` properties. With the PostgreSQL defaults except for the password, start it with:

```powershell
mvn -pl configured-server -Ppostgresql exec:exec -Dcdo.db.password=YOUR_PASSWORD
```

For SSL, also provide the keystore and passphrase properties from Section 2.2.2. The server
requires `cdo.ssl.keystore`; provide the truststore and passphrase values used by the Net4j SSL
configuration as appropriate for your keys. The client also needs its truststore settings from
Section 2.2.2. For WSS, provide the server keystore properties from Section 2.2.4; its keystore
type defaults to PKCS12 and can be changed with `cdo.ssl.keystore.type`. For example, use your
own keystore and password values with:

```powershell
mvn -pl configured-server exec:exec -Dtransport=wss `
  -Dcdo.wss.keystore=C:/certs/server.p12 `
  -Dcdo.wss.keystore.password=YOUR_KEYSTORE_PASSWORD
```

Values containing spaces can be passed as a single quoted Maven `-D` argument in PowerShell. Embedded quotation marks and other shell-sensitive characters may need shell-specific escaping. 

You can also run from `configured-server` itself with `mvn exec:exec`; the same properties and
defaults apply. The Java process inherits Maven's working directory and console streams. Enter `stop` and press Enter to shut the server down cleanly. Use a normal
PowerShell console for interactive shutdown. PowerShell ISE did not reliably deliver `stop` to
Maven's interactive input.

Maven `-D` options for `cdo.db.url`, `cdo.db.server`,
`cdo.db.port`, `cdo.db.database`, `cdo.db.user`, and `cdo.db.password` are passed as system
properties to the forked server. Unspecified values retain the fallbacks in
`config/cdo-server.xml`.

### 3.3 Run the Client

After building, run the existing `RemoteClient` from the examples directory:

```powershell
mvn -pl remote-client exec:exec
```

The client POM starts a separate Java process and forwards the `transport` system property and any supplied TLS, WSS, and WebSocket settings. Its `transport` property is independent of the server's Maven invocation. Set it to the same value as the server; it defaults to TCP. The optional client argument accepts a port, host
name, or `host:port`; without an argument, the defaults are `localhost:2036` for TCP/SSL and
`localhost:8080` or `localhost:8443` for WS/WSS.
To connect to another TCP server, pass its address through the Exec Maven Plugin:
`mvn -pl remote-client exec:exec -Dexec.application.args=server.example.org:2036`. For SSL, select the same
transport as the server and provide the client trust settings, for example:

```powershell
mvn -pl remote-client exec:exec -Dtransport=ssl `
  -Dcdo.ssl.truststore=C:/certs/cdo-trust.p12 `
  -Dcdo.ssl.passphrase=YOUR_TRUSTSTORE_PASSPHRASE
```

For WSS, the client uses the JSSE truststore properties:

```powershell
mvn -pl remote-client exec:exec -Dtransport=wss `
  -Dcdo.wss.truststore=C:/certs/cdo-trust.p12 `
  -Dcdo.wss.truststore.password=YOUR_TRUSTSTORE_PASSWORD
```

For WS/WSS, the client uses
`ws://<host>:<port>/net4j/@cdo` or `wss://<host>:<port>/net4j/@cdo`, matching the configured Jetty
route. For another server path, use `-Dcdo.ws.path=/` (or its configured service path) on the
client command.

For a PowerShell example that passes an application argument containing spaces, see the [embedded H2 example](../embedded-h2/README.md), which uses a database path rather than a host name.

On a fresh repository, `CREATED` means the client committed `/company` and its Company model data.
On a later invocation, `LOADED` means it found that resource in the repository and read the saved
objects. Both runs also print the Company, category, and products.

### 3.4 Verify Persistence

1. Start the server with the default command in Section 3.2.
2. Run the client; it should print `CREATED` after its transaction commits.
3. Run the client again; it should print `LOADED` because `/company` now exists.
4. Stop and restart the server without deleting `data/cdo`.
5. Run the client once more; it should print `LOADED` again.

This sequence demonstrates that CDO commits the model to the persistent H2 database and that the
repository restores it after a server restart.
