/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License 2.0.
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.doc.programmers.server;

import org.eclipse.emf.cdo.doc.operators.Doc00_OperatingServer;
import org.eclipse.emf.cdo.doc.operators.Doc02_ConfiguringAcceptors;
import org.eclipse.emf.cdo.spi.server.IAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension3;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;

/**
 * CDO Server Application and Startup
 * <p>
 * The packaged OSGi server application is {@code CDOServerApplication}. It is an application implementation, not a
 * general-purpose public API to subclass. Its useful programming contract is the lifecycle it gives application
 * extensions and the managed container that it uses to configure repositories and acceptors.
 * {@toc}
 *
 * @author Eike Stepper
 */
public class Doc02_ServerApplicationAndStartup
{
  /**
   * Startup and Shutdown
   * <p>
   * On startup the application obtains its managed container, reads the configured application extensions, and starts
   * extensions that request an early start. It then configures repositories from the server configuration, optionally
   * creates the configured browser, and starts the remaining extensions. An {@link IAppExtension3} receives the
   * configured repository array; a plain {@link IAppExtension} receives the configuration file.
   * <p>
   * On shutdown, ordinary extensions stop in reverse order, repositories deactivate, early extensions stop in reverse
   * order, and the application container deactivates. Exceptions from an individual extension are logged so that the
   * application can continue processing its other lifecycle participants. A missing configuration file is reported and
   * no repositories are configured.
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
   * OSGi supplies bundle and extension registration for the packaged application. A standalone or embedded Java
   * program can instead create repositories and Net4j elements directly in an
   * {@link org.eclipse.net4j.util.container.IManagedContainer}. Deployment details, configuration-file names, ports,
   * TLS, and production topology belong to {@link Doc00_OperatingServer} and {@link Doc02_ConfiguringAcceptors}.
   *
   * @see Doc03_ManagedContainer
   * @see Doc05_CreatingAndConfiguringRepositories
   */
  public class ExecutionModes
  {
  }
}
