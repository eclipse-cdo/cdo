/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.ui.views;

import org.eclipse.net4j.container.internal.ui.bundle.SharedIcons;
import org.eclipse.net4j.transport.IAcceptor;
import org.eclipse.net4j.transport.IBufferHandler;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IProtocol;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.registry.IRegistryEvent;

import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;
import java.util.Collection;

public class AcceptorsItemProvider extends ItemProvider<Container> implements IRegistryListener, IListener
{
  public AcceptorsItemProvider()
  {
  }

  public Object getParent(Object child)
  {
    if (child instanceof IAcceptor)
    {
      return getInput();
    }

    if (child instanceof IConnector)
    {
      IConnector connector = (IConnector)child;
      Collection<IAcceptor> acceptors = getInput().getAcceptorRegistry().values();
      for (IAcceptor acceptor : acceptors)
      {
        IConnector[] connectors = acceptor.getAcceptedConnectors();
        for (IConnector c : connectors)
        {
          if (c == connector)
          {
            return acceptor;
          }
        }
      }
    }

    if (child instanceof IChannel)
    {
      return ((IChannel)child).getConnector();
    }

    return null;
  }

  public Object[] getChildren(Object parent)
  {
    if (parent == getInput())
    {
      Collection values = getInput().getAcceptorRegistry().values();
      return values.toArray(new Object[values.size()]);
    }

    if (parent instanceof IAcceptor)
    {
      IAcceptor acceptor = (IAcceptor)parent;
      return acceptor.getAcceptedConnectors();
    }

    if (parent instanceof IConnector)
    {
      return ((IConnector)parent).getChannels();
    }

    return NO_CHILDREN;
  }

  public void notifyRegistryEvent(IRegistryEvent event)
  {
    refreshViewer(false);

    IRegistryDelta[] deltas = event.getDeltas();
    for (IRegistryDelta delta : deltas)
    {
      if (delta.getElement() instanceof IAcceptor)
      {
        IAcceptor acceptor = (IAcceptor)delta.getElement();
        switch (delta.getKind())
        {
        case REGISTERED:
          acceptor.addListener(this);
          break;

        case DEREGISTERED:
          acceptor.removeListener(this);
          break;
        }
      }
    }
  }

  public void notifyEvent(IEvent event)
  {
    refreshViewer(false);
  }

  @Override
  public String getText(Object obj)
  {
    if (obj instanceof IAcceptor)
    {
      IAcceptor acceptor = (IAcceptor)obj;
      return acceptor.getDescription();
    }

    if (obj instanceof IConnector)
    {
      IConnector connector = (IConnector)obj;
      return connector.getDescription();
    }

    if (obj instanceof IChannel)
    {
      IChannel channel = (IChannel)obj;
      IBufferHandler receiveHandler = channel.getReceiveHandler();
      Object str = receiveHandler instanceof IProtocol ? ((IProtocol)receiveHandler).getProtocolID() : receiveHandler;
      return MessageFormat.format("[{0}] {1}", channel.getChannelIndex(), str);
    }

    return super.getText(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof IAcceptor)
    {
      return SharedIcons.OBJ_ACCEPTOR.createImage();
    }

    if (obj instanceof IConnector)
    {
      return SharedIcons.OBJ_CONNECTOR.createImage();
    }

    if (obj instanceof IChannel)
    {
      return SharedIcons.OBJ_CHANNEL.createImage();
    }

    return null;
  }

  @Override
  protected void connectInput(Container input)
  {
    input.getAcceptorRegistry().addRegistryListener(this);
    input.getConnectorRegistry().addRegistryListener(this);
    input.getChannelRegistry().addRegistryListener(this);
  }

  @Override
  protected void disconnectInput(Container input)
  {
    input.getAcceptorRegistry().removeRegistryListener(this);
    input.getConnectorRegistry().removeRegistryListener(this);
    input.getChannelRegistry().removeRegistryListener(this);
  }
}