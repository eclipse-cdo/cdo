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
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.AsyncRequest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.StringRequest;
import org.eclipse.net4j.tests.signal.TestSignalClientProtocolFactory;
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
    container.registerFactory(new TestSignalClientProtocolFactory());
    container.registerFactory(new TestSignalServerProtocolFactory());
    return container;
  }

  public void testInteger() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_NAME, null);
    int data = 0x0a;
    int result = new IntRequest(channel, data).send();
    assertEquals(data, result);
  }

  public void testArray() throws Exception
  {
    startTransport();
    IChannel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_NAME, null);
    byte[] data = TinyData.getBytes();
    byte[] result = new ArrayRequest(channel, data).send();
    assertTrue(Arrays.equals(data, result));
  }

  public void testAsync() throws Exception
  {
    startTransport();
    // OMPlatform.INSTANCE.setDebugging(false);
    IChannel channel = getConnector().openChannel(TestSignalProtocol.PROTOCOL_NAME, null);
    String data = TinyData.getText();
    // String huge = HugeData.getText();
    for (int i = 0; i < 10000; i++)
    {
      msg("Loop " + i);
      new AsyncRequest(channel, data).send();
      String result = new StringRequest(channel, data).send();
      assertEquals(data, result);
    }
  }
}
