/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.SelfAttachingContainerListener;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;

import java.util.LinkedHashSet;

/**
 * @author Eike Stepper
 * @since 4.13
 */
public class ChannelContainer extends SetContainer<IChannel>
{
  private final IContainer<?> delegate;

  private final IListener delegateListener = new SelfAttachingContainerListener()
  {
    @Override
    public void attach(Object element)
    {
      if (element instanceof IChannel)
      {
        addChannel((IChannel)element);
      }

      super.attach(element);
    }

    @Override
    public void detach(Object element)
    {
      if (element instanceof IChannel)
      {
        IChannel channel = (IChannel)element;
        removeChannel(channel);
      }

      super.detach(element);
    }

    @Override
    protected boolean shouldDescend(Object element)
    {
      return element instanceof IManagedContainer || element instanceof IAcceptor || element instanceof IConnector;
    }

    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      INotifier source = event.getSource();
      if (source instanceof IChannel)
      {
        notifyChannelEvent(event);
      }
      else if (source instanceof IProtocol)
      {
        notifyProtocolEvent(event);
      }
    }
  };

  public ChannelContainer(IContainer<?> delegate)
  {
    super(IChannel.class, new LinkedHashSet<IChannel>());
    this.delegate = delegate;
  }

  protected void notifyChannelEvent(IEvent event)
  {
  }

  protected void notifyProtocolEvent(IEvent event)
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    delegate.addListener(delegateListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    delegate.removeListener(delegateListener);
    super.doDeactivate();
  }

  protected void addChannel(IChannel channel)
  {
    addElement(channel);

    IBufferHandler receiveHandler = channel.getReceiveHandler();
    if (receiveHandler instanceof IProtocol)
    {
      EventUtil.addUniqueListener(receiveHandler, delegateListener);
    }
  }

  protected void removeChannel(IChannel channel)
  {
    removeElement(channel);

    IBufferHandler receiveHandler = channel.getReceiveHandler();
    if (receiveHandler instanceof IProtocol)
    {
      EventUtil.removeListener(receiveHandler, delegateListener);
    }
  }
}
