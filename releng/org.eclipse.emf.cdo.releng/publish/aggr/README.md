# CDO Maven aggregation prototype

This directory contains the portable CBI `p2repo-aggregator` model for CDO.
It consumes a supplied CDO p2 drop (a promoted repository is not required for a
local run), and it neither builds CDO nor publishes to Maven Central.

## Required input

The canonical workflow is Ant and can be launched from Eclipse External Tools,
Windows command line, or later a Unix-like Jenkins agent:

```text
ant -Dcdo.drop.dir="C:\path\to\promoted\CDO-drop" -f build.xml validate
ant -Dcdo.drop.dir="C:\path\to\promoted\CDO-drop" -f build.xml clean-build
```

The Ant properties/arguments are:

* `cdo.drop.dir` — required. The promoted, unhidden CDO p2 drop containing
  `build-info.xml` and `tp-macro.setup`. The build fails immediately if the
  property is omitted or does not name an existing directory.
* `work.dir` — optional output/work area for the generated model, metadata,
  CBI configuration, workspace, and repository. If omitted, a unique folder
  below the system temporary directory is used. Pass
  `-Dwork.dir="C:\path\to\work"` to retain a stable location.
* `cbi.aggregator` — path to the CBI console executable. The portable default
  is `cbiAggrc.exe`; override it with `-Dcbi.aggregator=...` when the
  executable is not on the working directory/PATH.
* `maven.local.repository` — optional local Maven cache used both for the
  Eclipse/EMF dependencies needed while generating Javadoc. It defaults to
  `${user.home}/.m2/repository`; set it explicitly when Ant is launched from
  Eclipse with a different Java user home.
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
CBI validation/CLEAN_BUILD, classifier generation, consumer tests, audits, and
local staging accept all three types. The drop name or filesystem path is not
used to decide whether a build may be published.

`publishing.mode` selects the explicit policy gate (default:
`VALIDATE_ONLY`):

* `VALIDATE_ONLY` runs the normal technical checks and accepts R, S, and I.
* `DRY_RUN` is an upload-free staging mode and accepts R, S, and I.
* `PUBLISH` is the future Maven Central upload entry point and is accepted only
  for an R build. S and I builds fail immediately with a clear diagnostic;
  missing or unknown build types fail closed. There is no generic force flag.

Run the policy tests with `ant -f build.xml policy-test`. The `publish-policy`
target performs all local checks before applying the gate. A `publish` target is
present as the reserved upload entry point; it currently stops after a
successful R-only policy check because no uploader or credentials are
configured. Consequently, no target uploads artifacts or creates PGP
signatures. A later uploader must call the same policy gate immediately before
its network operation.

The generated CBI model enables the native Maven checks
`validateNexusPublishingRequirements="true"` and
`validatePOMDependencies="true"` on the root `aggregator:Aggregation` element.
`versionFormat="MavenRelease"` keeps generated Maven versions release
compatible. `includeSources="true"` is used together with source IUs generated
from the same central bundle map; CBI requires those IUs when validating a
binary bundle that contains class files.

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
ant -Dcdo.drop.dir="C:\path\to\promoted\CDO-drop" -Dwork.dir="C:\path\to\work" -f build.xml clean-build
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

The `sources-javadoc` target extracts the matching `*.source` plug-in from the
selected drop and creates non-empty `-sources.jar` and `-javadoc.jar` files for
all twelve aggregated artifacts. It validates the source plug-in's declared
bundle name and exact version against the binary plug-in before generation.
Javadoc uses the running JDK and the configured Maven cache for its Eclipse/EMF
classpath; documentation warnings are reported but do not fail a successful
Javadoc invocation. The H2 adapter also does not publish the H2 driver;
consumers must declare `com.h2database:h2` explicitly.

The `checksums` target runs after `sources-javadoc` and is included in
`build`. It creates and validates the same lowercase raw-hex sidecar format
used for the main JARs and POMs: `.md5`, `.sha1`, `.sha256`, and `.sha512`.
Every generated source/Javadoc classifier and its four checksum files must
validate before the target succeeds. It writes only below `work/repository/final`; PGP signatures
and Maven Central publication are not performed.

## Consumer compatibility test

After a successful aggregation, run `consumer-test` to resolve the twelve
CDO/Net4j versions from `repository/final`, write them to
`<work.dir>/consumer-test/cdo-consumer-versions.properties`, and execute the
Plain-Java Maven test. `consumer-dependency-tree` performs the same setup and
prints the resulting dependency tree. Both targets pass the generated values
to Maven as individual `-D` properties; no generated files are written into
the versioned consumer source tree.

## Model scope

`CDO.aggr.template` selects the 12 approved runtime Bundle-SymbolicNames and,
at generation time, their matching `.source` IUs from the same central
`BUNDLES` map. The source IUs are validation inputs; the later source/Javadoc
step creates the two Maven classifiers explicitly.
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

The validation repositories are extracted at runtime from the promoted drop's
`tp-macro.setup`, preserving order and removing duplicates. The resolved list
used by CBI is saved as `cdo-validation-repositories.txt`; moving URLs such as
`latest` are preserved and reported as reducing long-term reproducibility.
The promotion step replaces the fixed CDO token with the promoted drop URL.
For a locally supplied drop that has not yet gone through promotion/unhide, the
generated validation model safely substitutes the matching local drop directory
for that one CDO validation repository; other validation repositories remain
the dynamically extracted URLs. This permits a complete local dry run without
pretending that the drop is already published.
The aggregation-side generator retains a legacy fallback that resolves the
unreplaced catalog URL to its p2 child (`.../updates/releases/latest`).
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

* `cdo.build.drop`
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

The `sources-javadoc` and `checksums` targets are included in `build`, so a complete run produces
the classes, POMs, metadata, source JARs, and Javadoc JARs below the same
`repository/final` directory. The generated repository is suitable for serving
for local consumption after the independent consumer test passes; Maven
Central publication additionally requires a passing readiness audit and the
later signing/release steps.

## Maven Central readiness audit and local staging

Run `audit` after `checksums` to validate the generated coordinates, POM
dependencies, traceability properties, SCM commit, classifier files, and all
classifier checksums:

```text
ant -Dcdo.drop.dir="C:\path\to\drop" -Dwork.dir="C:\path\to\work" -f build.xml audit
```

The audit writes `maven-central-readiness-audit.txt` and a complete local
staging copy below `staging`. It never uploads anything and never creates PGP
signatures. The native CBI Nexus check runs before the portable post-generation
overlay; an unmodified CDO drop can therefore fail it with missing `url`,
`licenses`, `developers`, or `scm.url`. That is an ordering/metadata limitation,
not a disabled check. The overlay then supplies these values from the central
`publishing.properties` file, and the local readiness audit validates the
resulting POMs. The audit report records the drop ID, Git commit, artifact
counts, signature status, and every finding.

Source and Javadoc archives are assembled in sorted path order with normalized
ZIP entry timestamps, so repeated runs against the same drop are byte-stable
for these generated archives. The audit still records any repository-level
differences rather than masking them.
