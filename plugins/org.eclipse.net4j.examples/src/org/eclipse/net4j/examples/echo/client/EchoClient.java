/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.examples.echo.client;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class EchoClient
{
  public static void main(String[] args) throws Exception
  {
    // Send all traces and logs to the console
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    // Use this container to create and wire the components
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    container.activate();

    try
    {
      // Start a connector that represents the client side of a physical connection
      IConnector connector = Net4jUtil.getConnector(container, "tcp", "localhost:2036"); //$NON-NLS-1$ //$NON-NLS-2$

      // Open a virtual channel with the ECHO protocol, send an ECHO request and close the channel
      EchoClientProtocol protocol = new EchoClientProtocol(connector);
      EchoRequest request = new EchoRequest(protocol, "My cool message"); //$NON-NLS-1$
      String echo = request.send();
      protocol.close();

      System.out.println();
      System.out.println("ECHO: " + echo); //$NON-NLS-1$
      System.out.println();
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }
}
