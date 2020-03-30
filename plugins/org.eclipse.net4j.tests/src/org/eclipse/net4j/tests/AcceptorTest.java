/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.spi.net4j.Acceptor;
import org.eclipse.spi.net4j.Acceptor.ConnectorPreparer;
import org.eclipse.spi.net4j.InternalConnector;

/**
 * @author Eike Stepper
 */
public class AcceptorTest extends AbstractConfigTest
{
  public void testDeferredAccept() throws Exception
  {
    for (int i = 0; i < 5; i++)
    {
      IOUtil.OUT().println(" RUN = " + i); //$NON-NLS-1$

      Acceptor acceptor = (Acceptor)getAcceptor();
      acceptor.setConnectorPreparer(new ConnectorPreparer()
      {
        @Override
        public void prepareConnector(InternalConnector connector)
        {
          sleep(500);
        }
      });

      IConnector connector = getConnector();
      connector.waitForConnection(DEFAULT_TIMEOUT);
      assertEquals(false, connector.isClosed());

      stopTransport();
      sleep(1000);
    }
  }
}
