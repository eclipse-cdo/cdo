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
import org.eclipse.net4j.tests.signal.TestSignalClientProtocolFactory;
import org.eclipse.net4j.tests.signal.TestSignalServerProtocolFactory;
import org.eclipse.net4j.util.container.ManagedContainer;

/**
 * @author Eike Stepper
 */
public class TCPConnectivityLoss
{
  /**
   * @author Eike Stepper
   */
  public static class Server
  {
    public static void main(String[] args) throws Exception
    {
      ManagedContainer container = new ManagedContainer();
      Net4jUtil.prepareContainer(container);
      TCPUtil.prepareContainer(container);
      container.registerFactory(new TestSignalServerProtocolFactory());
      container.activate();

      TCPUtil.getAcceptor(container, null);
      while (System.in.available() != 0)
      {
      }

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
      ManagedContainer container = new ManagedContainer();
      Net4jUtil.prepareContainer(container);
      TCPUtil.prepareContainer(container);
      container.registerFactory(new TestSignalClientProtocolFactory());
      container.activate();

      TCPUtil.getConnector(container, "192.168.1.36");
      while (System.in.available() != 0)
      {
      }

      container.deactivate();
    }
  }
}
