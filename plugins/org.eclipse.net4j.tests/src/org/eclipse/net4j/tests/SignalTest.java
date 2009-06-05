/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.AsyncRequest;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.StringRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author Eike Stepper
 */
public class SignalTest extends AbstractProtocolTest
{
  public SignalTest()
  {
  }

  public void testInteger() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      IConnector connector = startTransport();
      protocol = new TestSignalProtocol(connector);
      int data = 0x0a;
      int result = new IntRequest(protocol, data).send();
      assertEquals(data, result);
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testArray() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      IConnector connector = startTransport();
      protocol = new TestSignalProtocol(connector);
      byte[] data = TinyData.getBytes();
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testAsync() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      IConnector connector = startTransport();
      OMPlatform.INSTANCE.setDebugging(false);
      protocol = new TestSignalProtocol(connector);
      String data = TinyData.getText();
      for (int i = 0; i < 1000; i++)
      {
        msg("Loop " + i); //$NON-NLS-1$
        new AsyncRequest(protocol, data).sendAsync();
        String result = new StringRequest(protocol, data).send();
        assertEquals(data, result);
      }
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  public void testCloseSocketChannel() throws Exception
  {
    TestSignalProtocol protocol = null;

    try
    {
      IConnector connector = startTransport();
      protocol = new TestSignalProtocol(connector);

      closeSocketChannel((TCPConnector)getAcceptor().getAcceptedConnectors()[0]);
      sleep(1000);
      assertEquals(false, protocol.isActive());
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }
    }
  }

  private void closeSocketChannel(TCPConnector connector) throws IOException
  {
    Field field = ReflectUtil.getField(TCPConnector.class, "socketChannel");
    SocketChannel socketChannel = (SocketChannel)ReflectUtil.getValue(field, connector);
    socketChannel.close();
  }
}
