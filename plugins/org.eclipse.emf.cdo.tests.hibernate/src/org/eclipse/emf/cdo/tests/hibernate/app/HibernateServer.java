/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - extended testcase
 */
package org.eclipse.emf.cdo.tests.hibernate.app;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author Eike Stepper
 */
public class HibernateServer
{
  private static final String REPOSITORY_NAME = "repo1";

  private static final boolean TRACE_TO_CONSOLE = true;

  private static final boolean TRACE_TO_FILE = false;

  private static PrintStream traceStream;

  public static void main(String[] args) throws Exception
  {
    try
    {
      IManagedContainer container = initContainer();
      TCPUtil.getAcceptor(container, "0.0.0.0:2036"); // Start the JVM transport
      CDOServerUtil.addRepository(container, HbStoreRepositoryProvider.getInstance().createRepository(REPOSITORY_NAME,
          new HashMap<String, String>()));
      IOUtil.OUT().println();
      IOUtil.OUT().println("Hit any key to shut down...");
      while (System.in.read() == -1)
      {
        Thread.sleep(500);
      }

      LifecycleUtil.deactivate(container);
    }
    finally
    {
      IOUtil.closeSilent(traceStream);
    }
  }

  private static IManagedContainer initContainer() throws FileNotFoundException
  {
    // Turn on tracing
    OMPlatform.INSTANCE.setDebugging(false);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    if (TRACE_TO_FILE)
    {
      traceStream = new PrintStream("trace.txt");
      OMPlatform.INSTANCE.addTraceHandler(new PrintTraceHandler(traceStream));
    }

    if (TRACE_TO_CONSOLE)
    {
      OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    }

    // Prepare the standalone infra structure (not needed when running inside Eclipse)
    IManagedContainer container = ContainerUtil.createContainer(); // Create a wiring container
    TCPUtil.prepareContainer(container); // Prepare the TCP transport
    Net4jUtil.prepareContainer(container); // Prepare the Net4j kernel
    CDONet4jServerUtil.prepareContainer(container); // Prepare the CDO server
    container.activate();
    return container;
  }
}
