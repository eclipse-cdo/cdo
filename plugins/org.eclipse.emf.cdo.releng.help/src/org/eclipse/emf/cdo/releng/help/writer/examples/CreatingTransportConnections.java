/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.help.writer.examples;

import org.eclipse.emf.cdo.releng.help.writer.examples.ConnectingToRepositories.SetBufferCapacity;
import org.eclipse.emf.cdo.releng.help.writer.examples.CreatingTransportConnections.AddConfigurationParameters.SetConnectionTimeout;
import org.eclipse.emf.cdo.releng.help.writer.examples.Snippets.JMSConnector;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.http.server.IHTTPAcceptor;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.ManagedContainer;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.spi.net4j.Connector;
import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.SocketChannel;

/**
 * Creating Transport Connections
 * <p>
 * This tutorial outlines the steps needed to create a Net4j {@link IConnector} and connect it to an {@link IAcceptor}.
 * <p>
 * Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu nibh in erat dapibus accumsan. Aenean cursus
 * lacinia dictum. Mauris non sem sapien. Vivamus sem ante, posuere a rhoncus ac, varius in nisi. Sed pulvinar urna ac
 * est iaculis mattis. Ut eget massa felis, nec volutpat purus. In id aliquet mi. Duis euismod sapien sollicitudin nisi
 * vestibulum nec vulputate urna euismod. Proin pulvinar ornare nunc, ac auctor elit placerat eget. Integer eu erat ac
 * risus ultricies mattis vel nec nunc. Proin venenatis tellus sit amet dui congue nec vehicula urna sollicitudin. Donec
 * porta, risus eu auctor semper, ante lectus lobortis sem, a luctus diam dui eu sapien. Sed at metus et dolor tincidunt
 * convallis id a est. Donec quam nisl, scelerisque a feugiat id, mattis vel urna. Suspendisse facilisis, libero ac
 * ultricies dictum, mi sem feugiat purus, ac aliquam metus purus sed leo. Sed a viverra metus.
 * <p>
 * <b>Table of Contents</b> {@toc}
 * 
 * @author Eike Stepper
 */
public class CreatingTransportConnections
{
  /**
   * Select a Transport Type
   * <p>
   * Currently supported transport types are:
   * <ul>
   * <li><b>JVM</b> to connect to an {@link IJVMAcceptor} in the same Java Virtual Machine.
   * <li><b>TCP</b> to connect to an {@link ITCPAcceptor} by means of a {@link SocketChannel}.
   * <li><b>SSL</b> an extension to the TCP transport that adds TLS/SSL security.
   * <li><b>HTTP</b> to connect to an {@link IHTTPAcceptor} that is made available by a servlet.
   * </ul>
   * Continue with {@link SetConnectionTimeout}.
   * 
   * @see SetBufferCapacity
   */
  public class SelectTransportType
  {
  }

  /**
   * Setup a Wiring Container
   * <p>
   * Ut eget massa felis, nec volutpat purus. In id aliquet mi. Duis euismod sapien sollicitudin nisi vestibulum nec
   * vulputate urna euismod. Proin pulvinar ornare nunc, ac auctor elit placerat eget. Integer eu erat ac risus
   * ultricies mattis vel nec nunc.
   * <p>
   * {@link Snippets#snippet1() ContainerSetup.java}
   * <p>
   * Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu nibh in erat dapibus accumsan. Aenean cursus
   * lacinia dictum. Mauris non sem sapien. Vivamus sem ante, posuere a rhoncus ac, varius in nisi. Sed pulvinar urna ac
   * est iaculis mattis. Ut eget massa felis, nec volutpat purus. In id aliquet mi. Duis euismod sapien sollicitudin
   * nisi vestibulum nec vulputate urna euismod. Proin pulvinar ornare nunc, ac auctor elit placerat eget. Integer eu
   * erat ac risus ultricies mattis vel nec nunc. Proin venenatis tellus sit amet dui congue nec vehicula urna
   * sollicitudin. Donec porta, risus eu auctor semper, ante lectus lobortis sem, a luctus diam dui eu sapien. Sed at
   * metus et dolor tincidunt convallis id a est. Donec quam nisl, scelerisque a feugiat id, mattis vel urna.
   * Suspendisse facilisis, libero ac ultricies dictum, mi sem feugiat purus, ac aliquam metus purus sed leo. Sed a
   * viverra metus.
   */
  public class SetupWiringContainer
  {
  }

  /**
   * Add Configuration Parameters
   */
  public class AddConfigurationParameters
  {
    /**
     * Set the Buffer Capacity
     * <p>
     * {@link JMSConnector}
     */
    public class SetBufferCapacity
    {
    }

    /**
     * Set the Connection Timeout
     * <p>
     * Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam eu nibh in erat dapibus accumsan. Aenean cursus
     * lacinia dictum. Mauris non sem sapien. Vivamus sem ante, posuere a rhoncus ac, varius in nisi. Sed pulvinar urna
     * ac est iaculis mattis. Ut eget massa felis, nec volutpat purus. In id aliquet mi. Duis euismod sapien
     * sollicitudin nisi vestibulum nec vulputate urna euismod. Proin pulvinar ornare nunc, ac auctor elit placerat
     * eget. Integer eu erat ac risus ultricies mattis vel nec nunc. Proin venenatis tellus sit amet dui congue nec
     * vehicula urna sollicitudin. Donec porta, risus eu auctor semper, ante lectus lobortis sem, a luctus diam dui eu
     * sapien. Sed at metus et dolor tincidunt convallis id a est. Donec quam nisl, scelerisque a feugiat id, mattis vel
     * urna. Suspendisse facilisis, libero ac ultricies dictum, mi sem feugiat purus, ac aliquam metus purus sed leo.
     * Sed a viverra metus.
     */
    public class SetConnectionTimeout
    {
    }
  }
}

// ----------------------------------------------------------------------------- //

/**
 * @snippet
 */
class Snippets
{
  /**
   * @callout Create a separate {@link IManagedContainer}.
   * @callout Create a factory of <i>type</i> "jms" in the <i>productGroup</i> "org.eclipse.net4j.connectors".
   * @callout Create a JMS connector.
   * @callout The new container can not be used when inactive.
   */
  public void snippet1()
  {
    // Create a dedicated container instance
    IManagedContainer container = /* callout */new ManagedContainer();

    // Register your custom factories
    container.registerFactory( /* callout */new Factory("org.eclipse.net4j.connectors", "jms")
    {
      public IConnector create(String description) throws ProductCreationException
      {
        return /* callout */new JMSConnector(description);
      }
    });

    // Use utility classes to register additional factories
    Net4jUtil.prepareContainer(container);
    TCPUtil.prepareContainer(container);

    // Do not forget to activate the container before you use it
    /* callout */container.activate();
  }

  /**
   * @callout The channel must not be <code>null</code>.
   */
  public class JMSConnector extends Connector
  {
    public JMSConnector(String description)
    {
    }

    public void multiplexChannel(InternalChannel /* callout */channel)
    {
    }

    // snip
    @Override
    protected INegotiationContext createNegotiationContext()
    {
      return null;
    }

    @Override
    protected void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol)
        throws ChannelException
    {
    }
    // snap
  }
}
