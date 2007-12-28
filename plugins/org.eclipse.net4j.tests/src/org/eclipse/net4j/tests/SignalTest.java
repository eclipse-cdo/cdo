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

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.tests.signal.Request1;
import org.eclipse.net4j.tests.signal.Request2;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.tests.signal.TestSignalServerProtocolFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class SignalTest extends AbstractTransportTest
{
  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new TestSignalServerProtocolFactory());
    return container;
  }

  public void testInteger() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_NAME, null);
    int data = 0x0a;
    int result = new Request1(channel, data).send();
    assertEquals(data, result);
  }

  public void testArray() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_NAME, null);
    byte[] data = TinyData.getBytes();
    byte[] result = new Request2(channel, data).send();
    assertTrue(Arrays.equals(data, result));
  }
}
