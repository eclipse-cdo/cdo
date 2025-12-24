/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.apps;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public class TCPConnectivityLoss
{
  private static boolean stop;

  public static ManagedContainer createContainer()
  {
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.setDebugging(true);

    ManagedContainer container = new ManagedContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    container.activate();
    return container;
  }

  public static void sleep() throws Exception
  {
    System.out.println("Started: " + new Date()); //$NON-NLS-1$
    stop = false;
    int count = 0;
    while (System.in.available() == 0)
    {
      Thread.sleep(1000L);
      System.out.print("."); //$NON-NLS-1$
      if (++count % 80 == 0)
      {
        System.out.println();
      }

      if (stop)
      {
        System.out.println("Loss of connectivity: " + new Date()); //$NON-NLS-1$
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Server
  {
    public static void main(String[] args) throws Exception
    {
      ManagedContainer container = createContainer();
      TCPUtil.getAcceptor(container, null);
      sleep();
      container.deactivate();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Client
  {
    public static void main(String[] args) throws Exception
    {
      ManagedContainer container = createContainer();
      ITCPConnector connector = TCPUtil.getConnector(container, "192.168.1.35"); //$NON-NLS-1$
      connector.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          stop = true;
        }
      });

      sleep();
      container.deactivate();
    }
  }
}
