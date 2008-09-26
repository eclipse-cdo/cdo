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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class ChannelTest extends AbstractProtocolTest
{
  private IConnector connector;

  private List<TestSignalProtocol> protocols;

  public ChannelTest()
  {
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    connector = startTransport();
    protocols = new ArrayList<TestSignalProtocol>();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    for (TestSignalProtocol protocol : protocols)
    {
      protocol.close();
    }

    super.doTearDown();
  }

  protected TestSignalProtocol openTestSignalProtocol()
  {
    return new TestSignalProtocol(connector);
  }

  public void testOpenChannel() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      protocol = openTestSignalProtocol();
      assertActive(protocol);

      protocol.close();
      assertInactive(protocol);
    }
    finally
    {
      if (protocol != null)
      {
      }
    }
  }
}
