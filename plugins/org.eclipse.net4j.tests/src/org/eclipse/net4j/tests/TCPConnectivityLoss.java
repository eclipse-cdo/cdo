/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

/**
 * @author Eike Stepper
 */
public class TCPConnectivityLoss
{
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
    int count = 0;
    while (System.in.available() == 0)
    {
      Thread.sleep(1000L);
      System.out.print(".");
      if (++count % 80 == 0)
      {
        System.out.println();
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
      TCPUtil.getConnector(container, "192.168.1.35");
      sleep();
      container.deactivate();
    }
  }
}
