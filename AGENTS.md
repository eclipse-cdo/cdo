## Maven / Tycho Validation

Maven/Tycho is **not** the default validation path for normal CDO development tasks.

When the Eclipse MCP server is available, use Eclipse/JDT/PDE MCP for routine
compilation/build-state verification and Eclipse MCP JUnit for focused test execution.

Do **not** invoke Maven/Tycho merely to:
- compile or validate Java changes;
- check affected projects;
- inspect PDE/OSGi resolution;
- run ordinary CDO unit/integration tests.

Use Maven/Tycho only when:
- the user explicitly requests Maven/Tycho or CI-equivalent validation;
- the task specifically concerns the Tycho build, target definition, features, sites,
  packaging, publishing, or other Maven/Tycho-specific behavior;
- a final broad CI-equivalent build has been explicitly requested.

The rules below apply only after Maven/Tycho has intentionally been selected by one
of these criteria. They do not themselves authorize starting Maven/Tycho.

* CDO is a Tycho/PDE reactor build using Java 21. Never invoke Maven directly from an individual bundle.
* Start focused builds from `releng/org.eclipse.emf.cdo.releng.parent/pom.xml`.
* Every focused reactor must include `org.eclipse.emf.cdo:org.eclipse.emf.cdo.releng.tp`.
* Maven `-am` does not resolve OSGi dependencies (`Require-Bundle`, `Import-Package`, `Fragment-Host`). Do not assume that it provides the required bundle closure.
* Always use full `groupId:artifactId` selectors; artifact-only selectors can be ambiguous between plugins and features.
* When the required in-repository OSGi closure is small and obvious, build that closure with `-pl ... -am -DskipTests package`.
* Otherwise prefer the reliable plugin-only reactor using the target definition plus `org.eclipse.emf.cdo:org.eclipse.emf.cdo.plugins -amd -DskipTests package` rather than spending excessive effort deriving the closure.
* Use `-amd` with the plugins aggregator only; do not treat `-amd` on an individual bundle as OSGi downstream-impact analysis.
* Tycho tests require `verify` and an explicit `CDO_TESTS` value. `IntegrationTests` is the normal CI suite.
* Only `org.eclipse.emf.cdo.tests`, `org.eclipse.emf.cdo.tests.db`, and `org.eclipse.emf.cdo.tests.lm` execute tests through Maven; other test bundles are compile-only under Maven.
* `CDO_TESTS` supports class/prefix selection but not individual test methods.
* Maven is configured to ignore test failures. Always inspect current `target/surefire-reports/TEST-*.xml`; a successful Maven exit code alone does not prove that tests passed.
* Use a root `clean verify` only for broad/build/target/feature/site changes or final CI-equivalent verification.

## CDO Test Scenario Selection

- For external CDO test-scenario selection, do not guess property names, syntax, factory types, or capabilities. Read the canonical test-framework JavaDocs starting at `IScenario`; consult `RepositoryConfigFactory` for repository capabilities and `DBConfigFactory` for DB extensions.
- The primary system properties are `cdo.test.scenario`, `cdo.test.repository`, `cdo.test.session`, and `cdo.test.model`. For syntax, precedence, valid factory types, and capabilities, consult the canonical JavaDocs above.
- CDO tests launched through Eclipse MCP are ordinary JUnit tests and must use `pluginTest=false`.

## Installer example mirrors

Do not edit `*/examples.installer/examples/...` directly.

Changes there may be generated automatically from canonical example projects by project builders / Ant scripts. Only flag them if they were edited independently, diverge from the canonical source, or contain unrelated changes.
