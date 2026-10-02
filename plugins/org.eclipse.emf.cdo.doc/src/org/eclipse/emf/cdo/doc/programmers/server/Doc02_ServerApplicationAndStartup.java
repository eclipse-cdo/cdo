/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.doc.operators.Doc00_OperatingServer;
import org.eclipse.emf.cdo.doc.operators.Doc02_ConfiguringAcceptors;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension2;
import org.eclipse.emf.cdo.spi.server.IAppExtension3;
import org.eclipse.emf.cdo.spi.server.IAppExtension4;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;

/**
 * CDO Server Application and Startup
 * <p>
 * The packaged OSGi server application is {@code CDOServerApplication}. It is an internal application implementation,
 * not an application API to subclass. It owns orchestration: it starts the OSGi application lifecycle, uses the shared
 * plugin container, discovers application-extension contributions, configures the repositories, and coordinates
 * auxiliary server components. Application code enters through the {@link IAppExtension} extension point and through
 * repository/store configuration. Standalone programs assemble those components themselves; embedded applications
 * can use the higher-level embedded-repository API. These are different ownership models, not three ways to subclass
 * the packaged application.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_ServerApplicationAndStartup
{
  /**
   * Startup and Shutdown
   * <p>
   * The OSGi application lifecycle starts first; {@code CDOServerApplication} obtains the shared plugin container
   * through its container accessor. If the configured server
   * XML file exists, the application discovers {@code appExtension} contributions from the OSGi registry, removes
   * contributions superseded by a {@code predecessor}, instantiates them, and injects the container into extensions
   * that implement {@code ContainerAware}. It sorts early and normal extensions separately by priority; lower numeric
   * priorities start first, with the default priority used by extensions that do not implement
   * {@link IAppExtension4}. An {@link IAppExtension5} selects the early phase with
   * {@link IAppExtension5#startBeforeRepositories()}.
   * <p>
   * Early extensions run before repository configuration, so they can install container-level factories or services
   * but cannot assume repositories exist. The configured {@code RepositoryConfigurator} then reads the XML: it creates
   * each store and repository, applies properties and initial packages, registers the repository in the container, and
   * activates it. A configuration with no repository entries is allowed but logged. An invalid XML document, missing
   * store factory, or repository activation error propagates from startup. The OSGi application framework does not
   * then call this application's normal stop sequence as rollback, so components created before the error can require
   * operator cleanup or restart; validate configuration before deployment.
   * <p>
   * After repository setup, the application creates the optional browser component if configured, then starts normal
   * extensions. An {@link IAppExtension3} receives the configured repository array instead of the base file-only
   * callback. Base and early extensions receive the configuration file; an early extension must not assume that
   * repositories are ready. The optional browser is a container-owned component started before normal extensions.
   * Acceptor configuration is normally represented in server XML and creates its acceptors as container elements;
   * the application coordinates the configured repository setup rather than exposing acceptor startup as an extension
   * callback.
   * <p>
   * Shutdown stops normal extensions in reverse priority/start order, deactivates the repositories returned by initial
   * configuration, stops early extensions in reverse order, deactivates the shared application container, and then
   * stops the OSGi application. Each extension stop is attempted even when another throws; exceptions are logged and
   * shutdown continues. An extension whose start partially installed listeners before throwing is still responsible
   * for undoing that partial work: a failed start is logged and the application does not call stop as rollback.
   * <p>
   * If the configured XML file is absent, the application logs a warning, skips extension discovery, repositories,
   * browser, and normal extension start, then enters the running application wait. It does not silently create a
   * default repository.
   * <p>
   * Dynamically created repositories are managed by the repository-configuration manager and their dynamic extension
   * lifecycle, not appended to the initial repository array. Use {@link IAppExtension2} for per-dynamic-repository XML
   * callbacks; use repository-specific extension behavior only when its callback provides the needed repository
   * context. See {@link Doc04_ApplicationExtensions}.
   */
  public class Lifecycle
  {
  }

  /**
   * Early Extensions
   * <p>
   * {@link IAppExtension5#startBeforeRepositories()} separates extensions that must prepare container-level services
   * before repository configuration from extensions that need running repositories. {@link IAppExtension5#getName()}
   * supplies the lifecycle log name. {@link org.eclipse.emf.cdo.spi.server.IAppExtension4 priorities} order each
   * group; lower values start first and reverse stopping follows the final order.
   */
  public class ExtensionTiming
  {
  }

  /**
   * Standalone, Embedded, and OSGi Use
   * <p>
   * The packaged OSGi application uses the shared plugin container; its bundle registry supplies factories and
   * {@code appExtension} contributions, and the application owns shutdown of that shared container. A standalone
   * program owns an independent initialized container, creates/configures named Net4j elements and repositories, and
   * deactivates the container when done. An embedded repository API packages a local repository and client connection
   * lifecycle for an application that does not need to assemble the full server. See {@link Doc03_ManagedContainer}
   * and {@link Doc05_CreatingAndConfiguringRepositories} for those workflows. Deployment details, configuration-file names, ports,
   * TLS, and production topology belong to {@link Doc00_OperatingServer} and {@link Doc02_ConfiguringAcceptors}.
   *
   * @see Doc03_ManagedContainer
   * @see Doc05_CreatingAndConfiguringRepositories
   */
  public class ExecutionModes
  {
  }
}
