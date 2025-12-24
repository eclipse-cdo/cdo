/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.ui.Net4jItemProvider;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.ContainerView;
import org.eclipse.net4j.util.ui.views.IElementFilter;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public abstract class AbstractTransportView extends ContainerView implements IElementFilter
{
  private IAction newAction;

  public AbstractTransportView()
  {
  }

  @Override
  protected IManagedContainer getContainer()
  {
    return IPluginContainer.INSTANCE;
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new Net4jItemProvider(this)
    {
      @Override
      public void updateLabels(Object element)
      {
        super.updateLabels(element);

        if (element instanceof IChannel)
        {
          IChannel channel = (IChannel)element;
          updateLabels(channel.getMultiplexer());
        }
        else if (element instanceof IServerConnector)
        {
          IServerConnector connector = (IServerConnector)element;
          updateLabels(connector.getAcceptor());
        }
      }

      @Override
      protected ContainerItemProvider<IContainer<Object>>.LeafNode createLeafNode(Node parent, Object element)
      {
        ContainerItemProvider<IContainer<Object>>.LeafNode leafNode = super.createLeafNode(parent, element);

        if (element instanceof IChannel)
        {
          IChannel channel = (IChannel)element;

          IBufferHandler receiveHandler = channel.getReceiveHandler();
          if (receiveHandler instanceof IProtocol)
          {
            EventUtil.addListener(receiveHandler, leafNode);
          }
        }

        return leafNode;
      }

      @Override
      protected Node removeNode(Object element)
      {
        Node node = super.removeNode(element);
        if (node instanceof ContainerItemProvider.LeafNode)
        {
          @SuppressWarnings("unchecked")
          ContainerItemProvider<IContainer<Object>>.LeafNode leafNode = (ContainerItemProvider<IContainer<Object>>.LeafNode)node;

          if (element instanceof IChannel)
          {
            IChannel channel = (IChannel)element;

            IBufferHandler receiveHandler = channel.getReceiveHandler();
            if (receiveHandler instanceof IProtocol)
            {
              EventUtil.removeListener(receiveHandler, leafNode);
            }
          }
        }

        return node;
      }

      @Override
      protected void handleElementEvent(IEvent event)
      {
        super.handleElementEvent(event);

        INotifier source = event.getSource();
        if (source instanceof IProtocol)
        {
          IProtocol<?> protocol = (IProtocol<?>)source;
          updateLabels(protocol.getChannel());
        }
      }

      @Override
      public StyledString getStyledText(Object obj)
      {
        StyledString styledText = super.getStyledText(obj);

        if (obj instanceof IChannel)
        {
          IChannel channel = (IChannel)obj;
          decorateChannel(styledText, channel);
        }
        else if (obj instanceof IConnector)
        {
          IConnector connector = (IConnector)obj;
          decorateConnector(styledText, connector);
        }
        else if (obj instanceof IAcceptor)
        {
          IAcceptor acceptor = (IAcceptor)obj;
          decorateAcceptor(styledText, acceptor);
        }

        return styledText;
      }

      private void decorateChannel(StyledString styledText, IChannel channel)
      {
        decorateChannelInfraStructure(styledText, channel);
        decorateCounters(styledText, channel.getReceivedBytes(), channel.getSentBytes());
      }

      private void decorateConnector(StyledString styledText, IConnector connector)
      {
        Collection<IChannel> channels = connector.getChannels();
        if (channels.size() > 1)
        {
          long receivedBytes = 0;
          long sentBytes = 0;

          for (IChannel channel : channels)
          {
            receivedBytes += channel.getReceivedBytes();
            sentBytes += channel.getSentBytes();
          }

          decorateCounters(styledText, receivedBytes, sentBytes);
        }
      }

      private void decorateAcceptor(StyledString styledText, IAcceptor acceptor)
      {
        IConnector[] connectors = acceptor.getAcceptedConnectors();
        if (connectors.length > 1)
        {
          long receivedBytes = 0;
          long sentBytes = 0;

          for (IConnector connector : connectors)
          {
            for (IChannel channel : connector.getChannels())
            {
              receivedBytes += channel.getReceivedBytes();
              sentBytes += channel.getSentBytes();
            }
          }

          decorateCounters(styledText, receivedBytes, sentBytes);
        }
      }
    };
  }

  @Override
  protected Control createUI(Composite parent)
  {
    newAction = createNewAction(getShell(), getContainer());
    return super.createUI(parent);
  }

  @Override
  protected void fillLocalToolBar(IToolBarManager manager)
  {
    if (newAction != null)
    {
      manager.add(newAction);
    }

    super.fillLocalToolBar(manager);
  }

  protected abstract IAction createNewAction(Shell shell, IManagedContainer container);

  static void decorateChannelInfraStructure(StyledString styledText, IChannel channel)
  {
    IBufferHandler receiveHandler = channel.getReceiveHandler();
    if (receiveHandler instanceof IProtocol)
    {
      IProtocol<?> protocol = (IProtocol<?>)receiveHandler;

      Object infraStructure = protocol.getInfraStructure();
      if (infraStructure != null)
      {
        styledText.append("  " + infraStructure, StyledString.DECORATIONS_STYLER);
      }
    }
  }

  static void decorateCounters(StyledString styledText, long received, long sent)
  {
    styledText.append("  \u2190" + received + "  " + sent + "\u2192", StyledString.COUNTER_STYLER);
  }
}
