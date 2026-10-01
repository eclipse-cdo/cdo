## Avoid unjustified implementation downcasts

Production code should use API/SPI abstractions where they represent a real architectural boundary. Do not downcast such abstractions merely to reach implementation-specific functionality that properly belongs on that abstraction.

However, do not introduce new `Internal*` interfaces, SPI methods, adapters, or other abstraction layers solely to eliminate a downcast. Within implementation code, a downcast to a concrete implementation class is acceptable when the concrete type is an intentional implementation invariant or when the collaborating classes are inherently implementation-specific.

When deciding, prefer the simplest architecture that reflects the actual ownership and coupling:

- Fix or extend an existing abstraction when the required capability conceptually belongs to that abstraction and is useful beyond one implementation-specific caller.
- Keep implementation-specific collaboration concrete when introducing an abstraction would only mirror implementation details or serve a single tightly coupled implementation path.
- Tests may freely use and downcast to concrete implementation classes. Do not add production API/SPI solely for tests.

Do not hide questionable casts behind helpers, reflection, or artificial interfaces. The goal is correct abstraction boundaries, not elimination of casts as such.

## CDO Native/Legacy Model Compatibility in Tests

CDO tests must remain compatible with both **Native** and **Legacy** model configurations unless a test explicitly targets only one mode.

In Legacy Mode, generated EMF model objects are not necessarily CDO-native implementations. Therefore:

- **Do not down-cast objects returned as `CDOObject` to generated model interfaces.** For example, this is invalid in Legacy Mode:

  ```java
  Customer customer = (Customer)transaction.getObject(customerID);
  ```

  Convert through the underlying `EObject` when a generated model instance is needed, e.g. with `CDOUtil.getEObject(...)`.

- Conversely, when CDO-specific access is required for a generated `EObject`, use `CDOUtil.getCDOObject(...)`.

- **Do not use generated package singletons directly**, such as:

  ```java
  Model1Package.eINSTANCE
  ```

  Use the package supplied by the test framework instead, e.g.:

  ```java
  getModel1Package()
  ```

  The Native/Legacy `ModelConfig` provides the generated `EPackage` appropriate for the active test configuration.

When adding or generating tests, preserve this abstraction consistently. Never assume that the concrete Java object simultaneously implements both the generated model interface and `CDOObject`.


## CDO Test Resource Paths

- In tests, do not normally use complete model resource paths as hard-coded string literals. Fixed full paths can collide with resources created by other test cases.
- Construct test resource paths with `getResourcePath(String path)` so that the test framework can provide the test-specific resource location.
- Use a literal full resource path only when a test intentionally needs a specific globally fixed path and that requirement is explicit.

## CDO Test Placement and Base Classes

Tests in `org.eclipse.emf.cdo.tests` must use the established config-test hierarchy and package-based suite discovery.

- **Scenario-independent tests** must:
  - extend `org.eclipse.emf.cdo.tests.config.impl.PlainTest`;
  - be placed in `org.eclipse.emf.cdo.tests.plain`;
  - run through the synthetic `Scenario[PLAIN]` exactly once.

- **General scenario-dependent CDO tests** must be placed in `org.eclipse.emf.cdo.tests.general` and use the normal `ConfigTest` hierarchy, typically through `AbstractCDOTest` or another appropriate config-test base class.

- Do not add concrete general test classes directly to the root `org.eclipse.emf.cdo.tests` package.

- Keep tests in established special-purpose packages when that package is itself part of the suite organization, in particular `org.eclipse.emf.cdo.tests.bugzilla` and `org.eclipse.emf.cdo.tests.issues`.

- `org.eclipse.emf.cdo.tests.plain` and `org.eclipse.emf.cdo.tests.general` are discovered automatically by the test-suite infrastructure. Do not add explicit per-class registrations when package discovery already covers the test.

- `PlainTest` inheritance is the semantic criterion for plain-test routing. A registered `PlainTest` is excluded from normal configured scenarios and runs exactly once under `Scenario[PLAIN]`.

- Do not create new tests that directly extend JUnit `TestCase` when they can participate in the CDO config-test infrastructure. A direct `TestCase` is only appropriate for an exceptional test that cannot reasonably use `PlainTest`, for example because package-private production access requires the test to remain in a specific production package. Do not widen production API merely to satisfy this placement rule.

Before adding a new test, determine whether it needs repository/session/model scenario configuration. If it does not, use `PlainTest` in `.plain`; otherwise use the appropriate config-test base class and package.

## CDO Test Scenario Selection

- For external CDO test-scenario selection, do not guess property names, syntax, factory types, or capabilities. Read the canonical test-framework JavaDocs starting at `IScenario`; consult `RepositoryConfigFactory` for repository capabilities and `DBConfigFactory` for DB extensions.
- The primary system properties are `cdo.test.scenario`, `cdo.test.repository`, `cdo.test.session`, and `cdo.test.model`. For syntax, precedence, valid factory types, and capabilities, consult the canonical JavaDocs above.
- CDO tests launched through Eclipse MCP are ordinary JUnit tests and must use `pluginTest=false`.
- The few Lifecycle Management tests that require p2 at runtime are the exception and require a PDE Plug-in Test launch; this is not the normal CDO test mode.

## Installer example mirrors

Never modify files under these repository-relative paths unless the user explicitly instructs otherwise:

- `plugins/org.eclipse.emf.cdo.examples.installer/examples/`
- `plugins/org.eclipse.net4j.examples.installer/examples/`

These trees are generated/mirrored from canonical example projects by project builders / Ant scripts. Do not edit them directly. Only flag changes there if they were edited independently, diverge from the canonical source, or contain unrelated changes.

## CDO Documentation

For every task that reads or modifies files under `plugins/org.eclipse.emf.cdo.doc`, first read `plugins/org.eclipse.emf.cdo.doc/DOC-FRAMEWORK.md`. Treat that file as the repository-local reference for the documentation framework and established CDO documentation authoring conventions.
