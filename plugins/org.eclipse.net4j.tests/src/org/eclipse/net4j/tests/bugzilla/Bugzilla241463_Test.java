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
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.Net4jTransportInjector;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorInjector;
import org.eclipse.net4j.internal.tcp.TCPServerConnector;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.tests.signal.TestSignalClientProtocolFactory;
import org.eclipse.net4j.tests.signal.TestSignalServerProtocolFactory;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.security.RandomizerFactory;

import org.eclipse.internal.net4j.ExecutorServiceFactory;
import org.eclipse.internal.net4j.buffer.BufferProviderFactory;
import org.eclipse.internal.net4j.connector.Connector;

import org.eclipse.spi.net4j.InternalChannel;

/**
 * @author Eike Stepper
 */
public class Bugzilla241463_Test extends AbstractTransportTest
{
  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = new ManagedContainer();

    // Net4j
    container.registerFactory(new ExecutorServiceFactory());
    container.registerFactory(new BufferProviderFactory());
    container.registerFactory(new RandomizerFactory());
    container.addPostProcessor(new Net4jTransportInjector());

    // TCP
    container.registerFactory(new TCPSelectorFactory());
    container.registerFactory(new FakeAcceptorFactory());
    container.registerFactory(new TCPConnectorFactory());
    container.addPostProcessor(new TCPSelectorInjector());

    // Test
    container.registerFactory(new TestSignalServerProtocolFactory());
    container.registerFactory(new TestSignalClientProtocolFactory());
    return container;
  }

  public void testBugzilla241463() throws Exception
  {
    startTransport();

    Connector connector = (Connector)getConnector();
    connector.setOpenChannelTimeout(2000L);

    try
    {
      connector.openChannel(TestSignalClientProtocolFactory.TYPE, null);
      fail("TimeoutRuntimeException expected");
    }
    catch (TimeoutRuntimeException success)
    {
    }
    catch (Throwable wrongException)
    {
      fail("TimeoutRuntimeException expected");
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FakeAcceptorFactory extends TCPAcceptorFactory
  {
    @Override
    public TCPAcceptor create(String description)
    {
      TCPAcceptor acceptor = new TCPAcceptor()
      {
        @Override
        protected TCPServerConnector createConnector()
        {
          return new TCPServerConnector(this)
          {
            @Override
            public InternalChannel createChannel(int channelID, short channelIndex, String protocolID)
            {
              throw new ImplementationError("Simulated problem");
            }
          };
        }
      };

      acceptor.setAddress(ITCPAcceptor.DEFAULT_ADDRESS);
      acceptor.setPort(ITCPAcceptor.DEFAULT_PORT);
      return acceptor;
    }
  }
}
