/*
 * Copyright (c) 2008, 2011-2013, 2015, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config;

import java.io.Serializable;
import java.util.Set;

/**
 * A complete CDO test scenario consisting of exactly one repository configuration, one session configuration, and
 * one model configuration.
 * <p>
 * The test framework can select a scenario from system properties. The complete scenario is supplied with
 * {@code cdo.test.scenario}; alternatively, the three component properties
 * {@code cdo.test.repository}, {@code cdo.test.session}, and {@code cdo.test.model} specify the repository,
 * session, and model independently. The properties are resolved in this order:
 * <ol>
 * <li>{@code cdo.test.scenario}</li>
 * <li>the complete repository/session/model property triple</li>
 * <li>the existing serialized scenario returned by {@link org.eclipse.emf.cdo.tests.config.impl.Scenario#load()}</li>
 * <li>the built-in scenario returned by {@link org.eclipse.emf.cdo.tests.config.impl.Scenario#getDefault()}</li>
 * </ol>
 * The first two steps are the external override mechanism implemented by
 * {@link org.eclipse.emf.cdo.tests.config.impl.Scenario#createFromProperties()}. The latter two are used when no
 * external override is present.
 * <p>
 * The complete scenario property and the individual component properties must not be combined. If any individual
 * component property is present, all three individual properties are required. Empty, malformed, or unknown
 * specifications fail with an exception; they do not silently fall back to a different scenario.
 * <p>
 * A complete scenario specification has the following form:
 *
 * <pre>
 * &lt;repository-spec&gt;/&lt;session-spec&gt;/&lt;model-spec&gt;
 * &lt;config-spec&gt; ::= &lt;factory-type&gt;[:&lt;description&gt;]
 * </pre>
 *
 * The factory type identifies a managed-container factory in the corresponding configuration product group. The
 * optional description is interpreted by that factory. For example:
 *
 * <pre>
 * MEM/JVM/NATIVE
 * MEM:branches,clientIDs/JVM/NATIVE
 * H2:branches,ranges/JVM/NATIVE
 * </pre>
 *
 * The component form is equivalent when the three properties are set separately, for example
 * {@code cdo.test.repository=MEM:branches,clientIDs}, {@code cdo.test.session=JVM}, and
 * {@code cdo.test.model=NATIVE}.
 * <p>
 * Separators in a specification use the escaping rules of
 * {@link org.eclipse.net4j.util.StringUtil#unescape(String, char)}. In particular, {@code \s} escapes the active
 * separator ({@code /} in the complete scenario and {@code :} between a factory type and its description), while
 * {@code \\} denotes a literal backslash. The standard character escapes handled by {@code StringUtil} are also
 * decoded, and unrecognized escaped characters follow {@code StringUtil}'s behavior. A trailing backslash is
 * rejected while the specification is split. An escaped separator is decoded before the managed-container factory
 * receives the specification.
 * <p>
 * Generic repository descriptions are specified by
 * {@link org.eclipse.emf.cdo.tests.config.impl.RepositoryConfigFactory}. The DB test bundle extends that syntax with
 * its documented {@code DBConfigFactory} capabilities.
 *
 * @author Eike Stepper
 */
public interface IScenario extends ITestLifecycle, Serializable
{
  public IRepositoryConfig getRepositoryConfig();

  public IScenario setRepositoryConfig(IRepositoryConfig repositoryConfig);

  public ISessionConfig getSessionConfig();

  public IScenario setSessionConfig(ISessionConfig sessionConfig);

  public IModelConfig getModelConfig();

  public IScenario setModelConfig(IModelConfig modelConfig);

  public Set<IConfig> getConfigs();

  public Set<String> getCapabilities();

  public boolean isValid();

  public boolean alwaysCleanRepositories();

  public void save();
}
