/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.internal.server.bundle.CDOStandaloneServerApplication;

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;

/**
 * Starts the CDO server as a plain Java application. Launch this class as the Java main class with the CDO server
 * runtime and its required bundles on the class path. The server requires a readable regular configuration file.
 * <p>
 * By default, {@code config/cdo-server.xml} is resolved relative to the process working directory. The
 * {@code net4j.config} JVM system property selects another configuration directory, and the
 * {@code org.eclipse.emf.cdo.server.configFileName} JVM system property selects another filename. Resolution uses
 * {@link OMPlatform#getConfigFile(String)}. Set these values with {@code -D...} JVM options; they are not application
 * arguments. The current {@link #main(String[])} does not process its argument array.
 * <p>
 * After startup, enter {@code stop} and press Enter for orderly shutdown. Ctrl+C in a real terminal and SIGTERM in
 * headless environments such as Docker use the JVM shutdown hook for the same cleanup. Eclipse's Terminate action
 * may stop the JVM without running that orderly lifecycle and can cause a DB store recovery cycle on the next start.
 * <p>
 * Example, using the default filename in a selected configuration directory:
 *
 * <pre>
 * java -Dnet4j.config=/etc/cdo -cp "&lt;CDO server runtime class path&gt;" org.eclipse.emf.cdo.server.Main
 * </pre>
 *
 * @author Eike Stepper
 * @since 4.27
 */
public final class Main
{
  private Main()
  {
  }

  /**
   * Starts the standalone CDO server.
   *
   * @param args currently ignored; server settings are supplied through JVM system properties and {@code cdo-server.xml}
   * @throws Exception if the server cannot be started or the main thread is interrupted while waiting
   */
  public static void main(String[] args) throws Exception
  {
    // Enable logging.
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    new CDOStandaloneServerApplication().run();
  }
}
