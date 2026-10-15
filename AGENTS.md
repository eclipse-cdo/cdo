## Code Formatting: Logical Grouping and Blank Lines

### Logical Statement Grouping

- Separate distinct logical operations and processing phases with exactly one blank line.
- Keep semantically related statements together without intervening blank lines.
- Keep object creation, configuration, and immediate use in one contiguous block. Avoid interleaving operations on unrelated objects.
- Prepare dependencies before creating the objects that use them. Declare local variables close to their first use.
- Related declarations and operations may share a block when they form one coherent step.

### Control Flow and Braces

- Precede control-flow statements with one blank line, except immediately after an opening brace.
- For `if` and `switch`, omit the preceding blank line when the condition primarily uses a variable initialized immediately before them.
- Never insert blank lines immediately before or after an opening brace.
- Insert one blank line after a closing brace before the next independent statement, except when followed by another closing brace.
- Keep syntactically connected branches (`else`, `else if`, `catch`, `finally`) together without blank lines.
- Format `do`/`while` with `} while (condition);` on the same line.
- Separate independent `switch` case/default groups with one blank line. Keep consecutive labels belonging to the same group together.

## Java Visibility

Use Java visibility according to the actual API relationship.

- Use `private` when a member is only used by its declaring type.
- Use `public` when production code in another type needs to access the member.
- Use `protected` only when inheritance is genuinely part of the design.
- Do not use `protected` merely to obtain its package visibility.
- Avoid package-private visibility as an artificial package boundary.
- In particular, do not make production APIs package-private merely because all current callers happen to reside in the same package.
- Do not force tests into production packages merely to access package-private production code.
- Do not introduce production test seams solely to make a test easier to write.

## Java Type Layout

Nested types must always be placed at the end of their enclosing type.

This applies to:

- nested classes
- nested interfaces
- nested enums
- nested records

Do not place nested types between fields, constructors, or ordinary methods.

## Javadoc

Every Java type must have Javadoc.

This includes:

- top-level classes
- top-level interfaces
- top-level enums
- top-level records
- nested classes
- nested interfaces
- nested enums
- nested records

Every type Javadoc must contain:

```java
@author <Full Name>
```

Never generate one-line Javadoc comments.

Bad:

```java
/** Represents a capture result. */
```

Use normal multi-line Javadoc instead:

```java
/**
 * Represents a capture result.
 *
 * @author Eike Stepper
 */
```

There must always be an empty Javadoc line before the Javadoc tag list.

The empty line must contain only the normal Javadoc prefix:

```java
 *
```

For example:

```java
/**
 * Coordinates the Capture AI workflow for a pending paper.
 *
 * @author Eike Stepper
 */
```

Do not place `@author`, `@param`, `@return`, `@throws`, or other tags directly after the description without that empty Javadoc line.

Do not generate mechanical Javadoc that merely restates the Java declaration in prose.

Use correct capitalization for domain and technology names such as:

- CDO
- MCP
- AI
- PDF
- JSON
- SWT
- JFace
- Eclipse
- Java

## Avoid unjustified implementation downcasts

Production code should respect existing API/SPI abstraction boundaries. When code operates on an abstraction, use that abstraction consistently. Do not downcast to concrete implementations merely to access functionality that conceptually belongs to the abstraction.

Extend an existing abstraction when necessary to express its responsibilities, even if the required operation currently has only one caller. Do not introduce new `Internal*` interfaces, adapters, or other abstraction layers solely to eliminate a downcast.

Within implementation code, a downcast to a concrete implementation class is acceptable when the concrete type is an intentional implementation invariant or when the collaborating classes are inherently implementation-specific. Concrete implementation checks must reflect genuine implementation-specific behavior, not compensate for missing functionality in an existing abstraction.

When deciding, prefer the simplest architecture that reflects the actual ownership and coupling:

- Fix or extend an existing abstraction when the required capability conceptually belongs to the abstraction, regardless of the number of callers.
- Use an existing abstraction consistently rather than introducing implementation-specific branches with different semantics.
- Keep implementation-specific collaboration concrete when introducing an abstraction would only mirror implementation details or serve a tightly coupled implementation path.
- Tests may freely use and downcast to concrete implementation classes. Do not add production API/SPI solely for tests.

Do not hide questionable casts behind helpers, reflection, or artificial interfaces. The goal is correct abstraction boundaries, not elimination of casts as such.

## CDO Test Design and Execution

### CDO Native/Legacy Model Compatibility in Tests

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


### CDO Test Resource Paths

- In tests, do not normally use complete model resource paths as hard-coded string literals. Fixed full paths can collide with resources created by other test cases.
- Construct test resource paths with `getResourcePath(String path)` so that the test framework can provide the test-specific resource location.
- Use a literal full resource path only when a test intentionally needs a specific globally fixed path and that requirement is explicit.


### CDO Test Placement and Base Classes

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

### CDO Test Scenario Selection

- For external CDO test-scenario selection, do not guess property names, syntax, factory types, or capabilities. Read the canonical test-framework JavaDocs starting at `IScenario`; consult `RepositoryConfigFactory` for repository capabilities and `DBConfigFactory` for DB extensions.
- The primary system properties are `cdo.test.scenario`, `cdo.test.repository`, `cdo.test.session`, and `cdo.test.model`. For syntax, precedence, valid factory types, and capabilities, consult the canonical JavaDocs above.
- CDO tests launched through Eclipse MCP are ordinary JUnit tests and must use `pluginTest=false`.
- The few Lifecycle Management tests that require p2 at runtime are the exception and require a PDE Plug-in Test launch; this is not the normal CDO test mode.

### CDO Test Suite Execution

- Run complete CDO test suites through their established static `suite()` methods, such as `AllTestsMEMBranches.suite()` or `AllTestsMEMAudit.suite()`.
- Never substitute project-wide, package-wide, or automatic JUnit test-class discovery for an established CDO test suite.
- The `ConfigTestSuite` infrastructure applies scenario-specific `@Requires` and `@Skips` constraints during suite construction. Bypassing it can execute tests under unsupported repository configurations and produce misleading failures.
- Direct execution of individual test classes or methods is permitted for focused testing and diagnostics, but does not replace suite-based validation. Verify the effective scenario and required capabilities.
- Before starting a complete suite, verify that the JUnit launch targets the intended suite and honors its `suite()` method.
- If the available test launcher cannot execute the suite correctly, report this limitation instead of substituting a different launch mechanism or claiming full-suite validation.
- Always report the executed suite, effective scenario, test counts, failures, errors, and whether execution completed normally.
- Use ordinary JUnit with `pluginTest=false`, as specified above.

## CDO Installer example mirrors

Never modify files under these repository-relative paths unless the user explicitly instructs otherwise:

- `plugins/org.eclipse.emf.cdo.examples.installer/examples/`
- `plugins/org.eclipse.net4j.examples.installer/examples/`

These trees are generated/mirrored from canonical example projects by project builders / Ant scripts. Do not edit them directly. Only flag changes there if they were edited independently, diverge from the canonical source, or contain unrelated changes.

## CDO/Net4j Documentation

The following four documentation plug-ins share the same documentation framework and authoring conventions:

- `plugins/org.eclipse.emf.cdo.doc`
- `plugins/org.eclipse.net4j.doc`
- `plugins/org.eclipse.net4j.util.doc`
- `plugins/org.eclipse.net4j.db.doc`

For every task that reads or modifies files in any of these plug-ins, first read `releng/org.eclipse.emf.cdo.releng.help/DOC-FRAMEWORK.md`. Treat it as the authoritative repository-local reference for the shared documentation framework and established authoring conventions.

In particular, every intended `@snip` example must be explicitly embedded in the appropriate article, chapter, or section Javadoc through a valid `{@link}` reference. Declaring a snippet alone does not cause it to appear in the generated documentation. Follow the snippet integration rules in `DOC-FRAMEWORK.md`.