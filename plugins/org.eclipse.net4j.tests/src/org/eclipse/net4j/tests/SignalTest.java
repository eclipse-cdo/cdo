/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.tests.signal.Request1;
import org.eclipse.net4j.tests.signal.Request2;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.util.Net4jUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.transport.tcp.AbstractTCPConnector;
import org.eclipse.internal.net4j.transport.tcp.TCPAcceptorImpl;
import org.eclipse.internal.net4j.transport.tcp.TCPSelectorImpl;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eike Stepper
 */
public class SignalTest extends AbstractOMTest
{
  private BufferProvider bufferPool;

  private TCPSelectorImpl selector;

  private TCPAcceptorImpl acceptor;

  private AbstractTCPConnector connector;

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    bufferPool = Net4jUtil.createBufferPool((short)64);
    LifecycleUtil.activate(bufferPool);
    assertTrue(LifecycleUtil.isActive(bufferPool));

    selector = (TCPSelectorImpl)Net4jUtil.createTCPSelector();
    selector.activate();
    assertTrue(selector.isActive());

    acceptor = (TCPAcceptorImpl)Net4jUtil.createTCPAcceptor(bufferPool, selector);
    connector = (AbstractTCPConnector)Net4jUtil.createTCPConnector(bufferPool, selector,
        "localhost");
  }

  @Override
  protected void tearDown() throws Exception
  {
    try
    {
      if (connector != null)
      {
        connector.disconnect();
        assertFalse(connector.isActive());
        assertFalse(connector.isConnected());
        connector = null;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    try
    {
      acceptor.deactivate();
      assertFalse(acceptor.isActive());
      acceptor = null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    try
    {
      selector.deactivate();
      selector = null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    try
    {
      LifecycleUtil.deactivate(bufferPool);
      bufferPool = null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }

    super.tearDown();
  }

  public void testInteger() throws Exception
  {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    IRegistry<String, ProtocolFactory> registry = new HashMapRegistry();
    registry.register(new TestSignalProtocol.Factory());

    acceptor.setReceiveExecutor(threadPool);
    acceptor.setProtocolFactoryRegistry(registry);
    acceptor.activate();
    assertTrue(acceptor.isActive());

    connector.setReceiveExecutor(threadPool);
    connector.setProtocolFactoryRegistry(registry);
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel(TestSignalProtocol.PROTOCOL_ID);
    int data = 0x0a;
    int result = new Request1(channel, data).send();
    assertEquals(data, result);
  }

  public void testArray() throws Exception
  {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    IRegistry<String, ProtocolFactory> registry = new HashMapRegistry();
    registry.register(new TestSignalProtocol.Factory());

    acceptor.setReceiveExecutor(threadPool);
    acceptor.setProtocolFactoryRegistry(registry);
    acceptor.activate();
    assertTrue(acceptor.isActive());

    connector.setReceiveExecutor(threadPool);
    connector.setProtocolFactoryRegistry(registry);
    assertTrue(connector.connect(1000));

    Channel channel = connector.openChannel(TestSignalProtocol.PROTOCOL_ID);
    byte[] data = TinyData.getBytes();
    byte[] result = new Request2(channel, data).send();
    assertTrue(Arrays.equals(data, result));

  }
}
