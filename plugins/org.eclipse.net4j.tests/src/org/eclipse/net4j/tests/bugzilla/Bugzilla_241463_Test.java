/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.net4j.Net4jUtil;
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
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tcp.ssl.SSLUtil;
import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;

import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * See bug 241463.
 *
 * @author Eike Stepper
 */
public class Bugzilla_241463_Test extends AbstractTransportTest
{
  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = new ManagedContainer();
    Net4jUtil.prepareContainer(container);
    JVMUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);
    SSLUtil.prepareContainer(container);

    container.registerFactory(new FakeJVMAcceptorFactory());
    container.registerFactory(new FakeTCPAcceptorFactory());
    container.registerFactory(new FakeSSLAcceptorFactory());

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
          JVMServerConnector connector = new JVMServerConnector(client)
          {
            @Override
            public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
            {
              throw new RuntimeException("Simulated problem"); //$NON-NLS-1$
            }
          };

          connector.setName(client.getName());
          connector.setConfig(getConfig());
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
  public static final class JVM extends Bugzilla_241463_Test
  {
    @Override
    protected boolean useJVMTransport()
    {
      return true;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class TCP extends Bugzilla_241463_Test
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
  public static final class SSL extends Bugzilla_241463_Test
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
