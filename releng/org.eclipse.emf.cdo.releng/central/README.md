# CDO Maven aggregation prototype

This directory contains the portable CBI `p2repo-aggregator` model for CDO.
It consumes a supplied CDO p2 drop (a promoted repository is not required for a
local run), and it neither builds CDO nor publishes to Maven Central.

`CDO.aggr` is the checked-in Maven Central publication policy and `CDO.aggran`
is its checked-in Analyzer descriptor. Both can be opened directly with the
CBI Aggregator tooling; no preparation step generates or rewrites an
aggregation model. The static IU and validation-repository structures are
deliberate policy. Drop-specific repository locations are supplied at runtime
through CBI's native `${key}` substitution and the `MappedRepository`
description override `org.eclipse.cbi.p2repo.cdo-location`.
For IDE analysis, define `org.eclipse.cbi.p2repo.simrel-location` as an Eclipse string
substitution variables (or use the repository's normal target-platform setup);
headless Ant runs provide them as JVM properties.

## Required input

The canonical workflow is Ant and can be launched from Eclipse External Tools,
Windows command line, or later a Unix-like Jenkins agent:

```text
ant -Dcdo.drop.location="C:\path\to\promoted\CDO-drop" -f build.xml validate
ant -Dcdo.drop.location="C:\path\to\promoted\CDO-drop" -f build.xml clean-build
ant -f build.xml policy-test
ant -f build.xml stream-test
ant -f build.xml native-classifier-test
ant -f build.xml drop-selection-test
ant -Dgpg.executable=gpg -f build.xml signature-test
```

The Ant properties/arguments are:

* `cdo.drop.location` — required. A promoted, unhidden CDO p2 drop, supplied as
  either a local directory or an HTTP(S) URL, containing `build-info.xml` and
  `tp-macro.setup`. Drop resources are read relative to this location.
* `work.dir` — optional output/work area for metadata, CBI configuration,
  workspace, and repository. If omitted, Jenkins uses `$WORKSPACE/maven-publishing/work`;
  local runs use `central/target/central`.
* `cbi.aggregator` — path to the CBI console executable. The portable default
  is `cbiAggrc.exe`; override it with `-Dcbi.aggregator=...` when the
  executable is not on the working directory/PATH.
* `maven.local.repository` — optional local Maven cache used as the source for
  the isolated consumer-test repository. It defaults to
  `${user.home}/.m2/repository`; set it explicitly when Ant is launched with a
  different Java user home.
* `maven.consumer.repository` — optional isolated Maven cache for the consumer and
  dependency-tree targets. It defaults to `${work.dir}/maven-local`, avoiding
  stale or locked artifacts in a global cache. The Ant workflow passes it to
  Maven as `-Dmaven.repo.local`.
The operation is selected directly by the Ant target: `validate`, `build`, or
`clean-build`; no separate action property or wrapper script is required.

## Validation and publishing policy

The technical workflow is independent of the eventual Maven Central publishing
policy. The build type is read only from `build-info.xml` and is carried into
the generated metadata as `cdo.build.type` (`R`, `S`, or `I`). Aggregation,
CBI validation/CLEAN_BUILD, native classifier validation, consumer tests,
audits, and local staging accept all three types. The drop name or filesystem
path is not used to decide whether a build may be published.

`publishing.mode` selects the explicit policy gate (default:
`VALIDATE_ONLY`):

* `VALIDATE_ONLY` runs the normal technical checks and accepts R, S, and I.
* `DRY_RUN` is an upload-free staging mode and accepts R, S, and I.
* `PACKAGE_FOR_CENTRAL` prepares a signed bundle but does not upload or publish
  it. It is accepted only for an R build. S and I builds fail immediately with
  a clear diagnostic; missing or unknown build types fail closed. There is no
  generic force flag.

Run the policy tests with `ant -f build.xml policy-test`. The `publish-policy`
target performs the staging checks before applying the gate. The separate
`verify-package` target applies the same R-only rule directly to build metadata
and then validates an already signed staging tree without rebuilding it. No
target uploads to Sonatype or publishes to Maven Central.

## Jenkins and Central packaging

`Jenkinsfile` is a separate declarative entry point for the same Ant workflow.
It accepts only `PUBLISHING_MODE` and `CDO_DROP_ID`. Jenkins constructs
`https://download.eclipse.org/modeling/emf/cdo/drops/<CDO_DROP_ID>/`, discovers
the single `*-Site.zip` in its `zips/` directory, and extracts that archive as
the local p2 repository. It downloads only the small root metadata files needed
by the build (`build-info.xml`, `build-info.properties`, `tp-macro.setup`,
`web.properties`, `bookmarks.xml`, and `p2.index`) in addition to the Site ZIP;
individual bundle downloads and recursive directory mirroring are avoided. The
archive is checked for path traversal and for the required `content.jar`,
`artifacts.jar`, and `plugins` directory before Ant is invoked. The downloaded
drop is stored at
`$WORKSPACE/maven-publishing/drop`; the Ant work directory is the sibling
`$WORKSPACE/maven-publishing/work` directory. The drop ID is retained in
generated metadata and reports, but is not part of the path because Eclipse CI
deletes the workspace after each build and concurrent builds are disabled. The
drop must contain
`build-info.xml`, `tp-macro.setup`, and the P2 metadata/artifacts.
`VALIDATE_ONLY` and `DRY_RUN` require no signing credentials. The job runs
aggregation, CBI validation/CLEAN_BUILD, native classifier validation,
checksums, the readiness audit, staging validation, the consumer test, and the
dependency tree.

`CDO_DROP_ID` defaults to `default`. An empty value has the same effect: the
pipeline fetches the official
[`drops.txt`](https://download.eclipse.org/modeling/emf/cdo/drops/drops.txt)
file over HTTPS. Eclipse atomically regenerates it when a CDO drop is promoted;
it contains one drop ID per line. The pipeline selects the lexicographically
greatest line matching `R[0-9]{8}-[0-9]{4}`, ignoring I-drops, S-drops, and
malformed lines. It then uses that resolved ID for the ordinary download, Ant
arguments, metadata, audit, and summary. If the file cannot be fetched or has
no matching R-drop, the build fails; it does not fall back to an older
drop. Supplying an explicit drop ID, such as `R20260901-1311`, skips release
discovery and keeps the existing download path.

The audit creates the upload candidate below the work directory. It preserves
the normal Maven path layout and includes each main, POM, source, and Javadoc
artifact together with its `.md5`, `.sha1`, `.sha256`, and `.sha512` sidecars.
`staging-validate` checks every JAR and POM and writes
`maven-publishing-summary.properties` with the drop, commit, build type,
artifact count, selected mode, and `central.publication.performed=false`.

For `PACKAGE_FOR_CENTRAL`, the Jenkinsfile binds the Jenkins Secret file
credential `secret-subkeys.asc` and pins the expected primary-key fingerprint
to `439BB94FD1FEF2D2615941A35604EF4E098FFAAE`. It imports the key into a
temporary workspace-local GnuPG home, verifies that primary identity, and
selects the unique usable signing-capable secret subkey under that primary.
The subkey fingerprint is passed to the Maven GPG Plugin Bouncy Castle signer;
the configured primary fingerprint remains the independent trust anchor for
package verification. The signing key is not copied into the repository or
archived.

The optional passphrase credential ID is blank. The signer runs with Maven GPG
Plugin best-practices mode enabled, so it does not load a passphrase from
`settings.xml` or project configuration. If the secret subkey is encrypted,
the plugin can use an explicitly configured Jenkins secret-text credential
through `MAVEN_GPG_PASSPHRASE` or an already-cached GnuPG agent passphrase; the
pipeline does not require or guess a passphrase credential.

The package stage signs the twelve approved POMs and their main, sources, and
Javadoc JARs into a local `file:` repository. It verifies all 48 detached
signatures with GnuPG against the independently configured primary
fingerprint, including signing subkeys, compares artifacts and checksums with
the original staging tree, then checks the exact Sonatype ZIP inventory and
entry bytes. Verification does not rebuild or rewrite staging. Only a
successful `PACKAGE_FOR_CENTRAL` build archives the deployment ZIP. There is
no Sonatype uploader or automatic publication step.

Every generated path is rooted under the canonical Jenkins `$WORKSPACE`. The
pipeline rejects workspace-root, empty, noncanonical, and symlink-resolved
output paths before cleanup or extraction. The original drop is only fetched
with explicit HTTP(S) `GET` requests; all extraction and copying use its local
workspace download. The agent must provide GNU `realpath`, `find`, `awk`,
`zipinfo`, and `tar` in addition to the tools listed below. No Ant cleanup
target removes paths outside its validated work directory.

The pipeline deliberately uses `agent any` because this repository cannot
derive a stable Eclipse CI label. The selected agent must nevertheless be a
Linux/POSIX node with the configured Jenkins tools `temurin-jdk25-latest`,
`apache-ant-latest`, and `apache-maven-latest`, plus `sh`, `curl`, `unzip`,
`zip`, `sha256sum`, GNU `realpath`, `find`, `awk`, `zipinfo`, `tar`, GnuPG
(`gpg` and `gpgconf`), and standard Jenkins Pipeline
credentials support. The selected Linux agent must also be able to start and
stop its isolated GnuPG agent. An Eclipse CI administrator can constrain the
job to its approved Linux/Kubernetes label without changing this repository.
No Windows paths, PowerShell, or globally installed CBI binary are assumed by
the workflow.

The Jenkins work directory is
`$WORKSPACE/maven-publishing/work`.
Eclipse CI deletes the complete workspace after every build, so no cross-build
cache is needed. The CBI archive is
downloaded and verified into `$WORKSPACE/cbi` for the current build only;
it is separate from Ant's cleaned work directory. The
Ant fallback remains a unique system temporary directory for local invocations
where `WORKSPACE` is unavailable. `disableConcurrentBuilds()` prevents two
builds of the same job from using that directory concurrently.

The Jenkins default downloads the latest successful CBI Linux nightly product
from
`https://download.eclipse.org/cbi/updates/p2-aggregator/products/nightly/latest/org.eclipse.cbi.p2repo.cli.product-linux.gtk.x86_64.tar.gz`.
The HTTPS download is stored below the workspace, then the archive paths and
entry types are validated before extraction. The workflow extracts either
`cbiAggr` (the Linux archive name) or `cbiAggrc`. The nightly URL is a moving
reference by design and is not protected by a hardcoded archive checksum.
The Jenkinsfile has only two job parameters: `PUBLISHING_MODE` and
`CDO_DROP_ID`. The CBI URL, workspace layout, tool names, signing credential
IDs and expected fingerprint are pipeline constants in the Jenkinsfile.
Local manual runs continue to use
`-Dcdo.drop.location=<local-or-http-drop>` and `-Dcbi.aggregator=...` through Ant; neither
local override is exposed as a Jenkins parameter.

The checked-in `CDO.aggr` model enables the native Maven checks
`validateNexusPublishingRequirements="true"` and
`validatePOMDependencies="true"` on the root `aggregator:Aggregation` element.
`versionFormat="MavenRelease"` keeps Maven versions release-compatible.
`includeSources="true"` is used together with the statically declared source
IUs; CBI requires those IUs when validating a binary bundle that contains class
files.

## CBI Maven metadata checks

The installed CBI 1.1.0 Maven engine has no POM template or arbitrary POM
metadata section in the aggregation model. Its supported inputs are the IU
name/description, IU license/copyright data, and bundle-manifest headers. In
particular, `Eclipse-SourceReferences` supplies the SCM, GitHub project URL,
contributors and issue-management data, while `Bundle-License` supplies the
Maven license. The engine performs the Nexus check while it is constructing
each POM, before that POM is written. The CBI source also has no separate
command for re-running this Nexus check against an already-overlaid local
Maven repository; `validatePOMDependencies` is likewise part of the build's
post-generation repository filtering step.

The normal CDO Tycho parent now supplies the supported inputs centrally:
`tycho.scmUrl` points to the GitHub repository, Tycho's JGit source-reference
provider generates the actual commit and bundle-relative path, and the parent
POM's EPL-2.0 license is converted to `Bundle-License` by
`tycho-packaging-plugin`'s `deriveHeaderFromProject` support. This applies to
every Tycho bundle project, including UI, test, example, server, dialect and
source bundles, without a bundle list. The old promoted drop remains
unchanged, so it still cannot pass the native Nexus check; a newly generated
drop is required to verify the corrected manifests. The portable overlay remains
the canonical project-specific step and supplies final values from
`publishing.properties`, including the `https://eclipse.dev/cdo/` project URL.

For example:

```text
ant -Dcdo.drop.location="C:\path\to\promoted\CDO-drop" -Dwork.dir="C:\path\to\work" -f build.xml clean-build
```

## Maven repository output

The `clean-build` target runs validation, performs a clean CBI build, overlays
the CDO traceability properties into every generated POM, and verifies the
result. A successful run produces a Maven-compatible repository at:

```text
<work.dir>\repository\final\
```

The directory can be served or published after the build completes; the Ant
workflow does not publish it automatically. If `work.dir` is omitted, the
repository is under the per-run system temporary directory. For a repeatable
location, pass `-Dwork.dir="C:\path\to\work"`.

Immediately after CBI's clean aggregation, `native-classifier-validation`
checks the raw binary JAR, POM, sources JAR, and Javadoc JAR for each approved
coordinate before the metadata overlay. Sources archives must be readable and
contain nonempty Java sources. The native CBI Javadoc archives must contain
their manifest and README, whose source reference must identify the release
commit and corresponding plug-in. This is a read-only validation; CBI is the
only source of these classifiers. The H2 adapter does not publish the H2
driver; consumers must declare `com.h2database:h2` explicitly.

The `checksums` target follows POM verification and is included in `build`.
For the sources and Javadoc classifiers, it preserves correct existing CBI
sidecars, creates only missing required `.md5`, `.sha1`, `.sha256`, and
`.sha512` sidecars, and rejects incorrect existing values. The audit validates
these along with the main JAR and POM checksums before staging. The target
writes only below `work/repository/final`; PGP signatures and Maven Central
publication are not performed by this technical target.

## Consumer compatibility test

After a successful aggregation, run `consumer-test` to resolve the twelve
CDO/Net4j versions from `repository/final`, write them to
`<work.dir>/consumer-test/cdo-consumer-versions.properties`, and execute the
Plain-Java Maven test. `consumer-dependency-tree` performs the same setup and
prints the resulting dependency tree. Both targets pass the generated values
to Maven as individual `-D` properties; no generated files are written into
the versioned consumer source tree.

## Model scope

`CDO.aggr` statically selects the approved runtime Bundle-SymbolicNames and
their matching `.source` IUs. CBI's Maven result creates the sources and
Javadoc classifiers from those inputs. Structural changes to this list require
a deliberate edit to `CDO.aggr`.
The sole Maven mapping fixes their groupId to `org.eclipse.cdo` and retains the
exact Bundle-SymbolicName as artifactId. No features, UI, tests, examples,
documentation, JDBC drivers, or database dialects other than H2 are selected.
The root `includedIUPattern` is an additional guard: it prevents p2's greedy
optional H2-driver requirement from being copied as `com.h2database`.
EMF dependencies retain their published `org.eclipse.emf` coordinates.
Eclipse Core requirements use the same `org.eclipse.platform` Maven mapping as
the EMF publication model.

Dependency mapping follows the CBI/Eclipse conventions: the selected CDO and
Net4j bundles use `org.eclipse.cdo`, EMF dependencies use `org.eclipse.emf`,
and Eclipse Platform dependencies use `org.eclipse.platform`. External
Orbit/JDK support bundles are referenced under their established Maven
coordinates and are not copied into this repository. The audit classifies
internal CDO, EMF, Platform, external/Orbit, JDBC, and synthetic `p2.osgi`
coordinates separately and fails on synthetic or unpublished internal
dependencies. Import-Package requirements remain represented by the provider
bundles selected by the CBI model; an external provider is not silently
re-published under a CDO coordinate.

The H2 adapter is published without the H2 driver. The consumer declares
`com.h2database:h2` explicitly because the adapter's driver import is optional
in the selected OSGi configuration. This keeps the driver available from its
normal Maven Central coordinate without redistributing it here.

The preparation step extracts the promoted drop's `tp-macro.setup` and derives
exactly two runtime properties: `org.eclipse.cbi.p2repo.cdo-location` (the
selected drop repository) and `org.eclipse.cbi.p2repo.simrel-location` (the
SimRel validation repository). They are passed to the CBI JVM with
`-vmargs -D...`; the Ant `--buildRoot` option supplies run-local output without
modifying the checked-in model. The CDO MappedRepository uses the description
`cdo-location`; headless publishing must provide its override explicitly.

Preparation prints every supplied value for diagnostics. CBI 1.1.0 may report
raw `p2:${key}` text in its generic repository error even after resolving the
location; diagnose failures against the resolved URL and its repository
contents, not against that wording alone.

They provide the external Eclipse, EMF, Orbit, and Java execution-environment
IUs needed to validate dependency closure, but are never selected or copied to
the Maven result. This prevents silently copying or assigning Maven coordinates
to unverified dependencies.

## Traceability

The CBI aggregation model supports coordinate and version mappings, but it has
no model field for arbitrary Maven POM properties. After the first generated
POMs are inspected, the smallest suitable extension is a post-generation XML
POM overlay that adds these properties from `build-info.xml` and the supplied
drop identity:

* `cdo.drop.id`
* `cdo.eclipse.simrel`
* `cdo.jenkins.job`
* `cdo.jenkins.build`
* `cdo.git.commit`
* `cdo.build.branch`
* `cdo.eclipse.version`
* `cdo.emf.version`
* `cdo.jenkins.url`
* `cdo.build.stream`
* `cdo.build.timestamp`
* `cdo.build.trigger`
* `cdo.build.type`

The Ant `overlay` target adds these values to every generated POM with an
XML-aware DOM transformation. The `verify` target checks all twelve coordinates,
properties, commit identity, and excluded-artifact guards.

The native classifier validation and `checksums` targets are included in
`build`, so a complete run produces the binaries, POMs, metadata, sources JARs,
and Javadoc JARs below the same `repository/final` directory. The generated repository is suitable for serving
for local consumption after the independent consumer test passes; Maven
Central publication additionally requires a passing readiness audit and the
later signing/release steps.

## Maven Central readiness audit and local staging

Run `audit` after `checksums` to validate the generated coordinates, POM
dependencies, traceability properties, SCM commit, classifier files, and all
classifier checksums:

```text
ant -Dcdo.drop.location="C:\path\to\drop" -Dwork.dir="C:\path\to\work" -f build.xml audit
```

The audit writes `maven-central-readiness-audit.txt` and a complete local
staging copy below `staging`. It never uploads anything or creates PGP
signatures. The separate Jenkins package stage signs the completed staging
tree after the audit. The native CBI Nexus check runs before the portable post-generation
overlay; an unmodified CDO drop can therefore fail it with missing `url`,
`licenses`, `developers`, or `scm.url`. That is an ordering/metadata limitation,
not a disabled check. The overlay then supplies these values from the central
`publishing.properties` file, and the local readiness audit validates the
resulting POMs. The audit report records the drop ID, Git commit, artifact
counts, signature status, and every finding.

Source and Javadoc archives are generated by the CBI Maven aggregation from the
selected release inputs. The audit records repository-level differences rather
than masking them.
