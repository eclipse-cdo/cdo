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
package org.eclipse.net4j.examples.echo.client;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.examples.echo.EchoProtocol;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

/**
 * @author Eike Stepper
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

    try
    {
      Net4jUtil.prepareContainer(container);
      TCPUtil.prepareContainer(container);
      container.registerFactory(new EchoClientProtocol.Factory());
      LifecycleUtil.activate(container);

      // Start a connector that represents the client side of a physical connection
      IConnector connector = (IConnector)container.getElement("org.eclipse.net4j.connectors", "tcp", "localhost:2036");

      // Open a virtual channel with the ECHO protocol, send an ECHO request and close the channel
      IChannel channel = connector.openChannel(EchoProtocol.PROTOCOL_NAME, null);
      EchoRequest request = new EchoRequest(channel, "My cool message");
      String echo = request.send();
      channel.close();

      System.out.println();
      System.out.println("ECHO: " + echo);
      System.out.println();
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }
}
