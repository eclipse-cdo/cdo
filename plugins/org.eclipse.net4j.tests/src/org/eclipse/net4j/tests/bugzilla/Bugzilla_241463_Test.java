/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - maintenance (SSL)    
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.TransportInjector;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorInjector;
import org.eclipse.net4j.internal.tcp.TCPServerConnector;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptor;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnectorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLServerConnector;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.tests.ChannelTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.security.RandomizerFactory;

import org.eclipse.internal.net4j.buffer.BufferProviderFactory;

import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

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

    container.registerFactory(new TCPSelectorFactory());
    container.addPostProcessor(new TCPSelectorInjector());

    if (useSSLTransport())
    {
      // SSL
      container.registerFactory(new FakeSSLAcceptorFactory());
      container.registerFactory(new SSLConnectorFactory());
    }
    else
    {
      // TCP
      container.registerFactory(new FakeAcceptorFactory());
      container.registerFactory(new TCPConnectorFactory());
    }

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
              throw new RuntimeException("Simulated problem"); //$NON-NLS-1$
            }
          };
        }
      };

      acceptor.setAddress(ITCPAcceptor.DEFAULT_ADDRESS);
      acceptor.setPort(ITCPAcceptor.DEFAULT_PORT);
      return acceptor;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FakeSSLAcceptorFactory extends SSLAcceptorFactory
  {
    @Override
    public SSLAcceptor create(String description)
    {
      SSLAcceptor acceptor = new SSLAcceptor()
      {

        @Override
        public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
        {
          try
          {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null)
            {
              socketChannel.configureBlocking(false);

              SSLServerConnector connector = new SSLServerConnector(this)
              {
                @Override
                public InternalChannel inverseOpenChannel(short channelID, String protocolID)
                {
                  throw new RuntimeException("Simulated problem"); //$NON-NLS-1$
                }
              };
              prepareConnector(connector);
              connector.setSocketChannel(socketChannel);
              connector.setSelector(selector);
              connector.activate();
            }
          }
          catch (ClosedChannelException ex)
          {
            deactivateAsync();
          }
          catch (Exception ex)
          {
            deactivateAsync();
          }
        }
      };

      acceptor.setAddress(ITCPAcceptor.DEFAULT_ADDRESS);
      acceptor.setPort(ITCPAcceptor.DEFAULT_PORT);
      return acceptor;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends ChannelTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static final class SSL extends ChannelTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return true;
    }
  }
}
