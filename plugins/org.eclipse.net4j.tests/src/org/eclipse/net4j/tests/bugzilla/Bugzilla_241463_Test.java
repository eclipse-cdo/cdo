/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.TransportInjector;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorInjector;
import org.eclipse.net4j.internal.tcp.TCPServerConnector;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.security.RandomizerFactory;

import org.eclipse.internal.net4j.buffer.BufferProviderFactory;

import org.eclipse.spi.net4j.InternalChannel;

/**
 * @author Eike Stepper
 */
public class Bugzilla_241463_Test extends AbstractTransportTest
{
  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = new ManagedContainer();

    // Net4j
    container.registerFactory(new ExecutorServiceFactory());
    container.registerFactory(new BufferProviderFactory());
    container.registerFactory(new RandomizerFactory());
    container.addPostProcessor(new TransportInjector());

    // TCP
    container.registerFactory(new TCPSelectorFactory());
    container.registerFactory(new FakeAcceptorFactory());
    container.registerFactory(new TCPConnectorFactory());
    container.addPostProcessor(new TCPSelectorInjector());

    // Test
    container.registerFactory(new TestSignalProtocol.Factory());
    return container;
  }

  public void testBugzilla241463() throws Exception
  {
    startTransport();
    getConnector().setOpenChannelTimeout(2000L);

    try
    {
      new TestSignalProtocol(getConnector());
      fail("Exception expected"); //$NON-NLS-1$
    }
    catch (Exception expected)
    {
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
            public InternalChannel inverseOpenChannel(short channelIndex, String protocolID)
            {
              throw new ImplementationError("Simulated problem"); //$NON-NLS-1$
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
