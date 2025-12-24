/*
 * Copyright (c) 2010-2012, 2016, 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - maintenance (SSL)
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.internal.jvm.JVMAcceptor;
import org.eclipse.net4j.internal.jvm.JVMAcceptorFactory;
import org.eclipse.net4j.internal.jvm.JVMClientConnector;
import org.eclipse.net4j.internal.jvm.JVMServerConnector;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPServerConnector;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptor;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLServerConnector;
import org.eclipse.net4j.internal.ws.WSAcceptor;
import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.internal.ws.WSServerConnector;
import org.eclipse.net4j.internal.wss.WSSAcceptorFactory;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * See bug 241463.
 *
 * @author Eike Stepper
 */
public class Bugzilla_241463_Test extends AbstractConfigTest
{
  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new FakeJVMAcceptorFactory());
    container.registerFactory(new FakeTCPAcceptorFactory());
    container.registerFactory(new FakeSSLAcceptorFactory());
    container.registerFactory(new FakeWSAcceptorFactory());
    container.registerFactory(new FakeWSSAcceptorFactory());
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
  private static final class FakeJVMAcceptorFactory extends JVMAcceptorFactory
  {
    @Override
    public JVMAcceptor create(String description)
    {
      JVMAcceptor acceptor = new JVMAcceptor()
      {
        @Override
        public JVMServerConnector handleAccept(JVMClientConnector client)
        {
          JVMServerConnector connector = new JVMServerConnector(this, client)
          {
            @Override
            public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
            {
              throw new RuntimeException("Simulated problem"); //$NON-NLS-1$
            }
          };

          prepareConnector(connector);
          connector.setName(client.getName());
          connector.activate();
          addConnector(connector);
          return connector;
        }
      };

      acceptor.setName(description);
      return acceptor;
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class FakeTCPAcceptorFactory extends TCPAcceptorFactory
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
            public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
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
                public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
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
  private static final class FakeWSAcceptorFactory extends WSAcceptorFactory
  {
    @Override
    protected WSAcceptor createAcceptor()
    {
      return new WSAcceptor()
      {
        @Override
        protected WSServerConnector createConnector()
        {
          return new WSServerConnector(this)
          {

            @Override
            public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
            {
              throw new RuntimeException("Simulated problem"); //$NON-NLS-1$
            }
          };
        }
      };
    }
  }

  /**
   * @author Maxime Porhel
   */
  private static final class FakeWSSAcceptorFactory extends WSSAcceptorFactory
  {
    @Override
    protected WSAcceptor createAcceptor()
    {
      return new WSAcceptor()
      {
        @Override
        protected WSServerConnector createConnector()
        {
          return new WSServerConnector(this)
          {

            @Override
            public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
            {
              throw new RuntimeException("Simulated problem"); //$NON-NLS-1$
            }
          };
        }
      };
    }
  }
}
