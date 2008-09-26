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

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;

/**
 * @author Eike Stepper
 */
public class ChannelTest extends AbstractProtocolTest
{
  public ChannelTest()
  {
  }

  public void testOpenChannel() throws Exception
  {
    IConnector connector = startTransport();
    TestSignalProtocol protocol = null;

    try
    {
      protocol = new TestSignalProtocol(connector);
      protocol.close();
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }
}
