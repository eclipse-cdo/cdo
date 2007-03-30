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

import org.eclipse.net4j.internal.ui.bundle.SharedIcons;
import org.eclipse.net4j.transport.IBufferHandler;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IProtocol;
import org.eclipse.net4j.util.registry.IRegistryEvent;

import org.eclipse.swt.graphics.Image;

import java.text.MessageFormat;
import java.util.Collection;

public class ConnectorsItemProvider extends ItemProvider<Container> implements IRegistryListener
{
  public ConnectorsItemProvider()
  {
  }

  public Object getParent(Object child)
  {
    if (child instanceof IConnector)
    {
      return getInput();
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
      Collection values = getInput().getConnectorRegistry().values();
      return values.toArray(new Object[values.size()]);
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
  }

  @Override
  public String getText(Object obj)
  {
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
    input.getConnectorRegistry().addRegistryListener(this);
    input.getChannelRegistry().addRegistryListener(this);
  }

  @Override
  protected void disconnectInput(Container input)
  {
    input.getConnectorRegistry().removeRegistryListener(this);
    input.getChannelRegistry().removeRegistryListener(this);
  }
}