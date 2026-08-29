## Maven / Tycho Validation

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
