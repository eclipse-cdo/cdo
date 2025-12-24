/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.internal.jms.messages.Messages;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageConsumerImpl extends Lifecycle implements MessageConsumer
{
  private static final long DO_NOT_WAIT = -1L;

  private SessionImpl session;

  private long consumerID;

  private DestinationImpl destination;

  private String messageSelector;

  private MessageListener messageListener;

  /**
   * Incoming messages
   */
  private BlockingQueue<MessageImpl> messages = new LinkedBlockingQueue<>();

  public MessageConsumerImpl(SessionImpl session, long consumerID, DestinationImpl destination, String messageSelector)
  {
    this.session = session;
    this.consumerID = consumerID;
    this.destination = destination;
    this.messageSelector = messageSelector;
  }

  public SessionImpl getSession()
  {
    return session;
  }

  public long getConsumerID()
  {
    return consumerID;
  }

  public DestinationImpl getDestination()
  {
    return destination;
  }

  @Override
  public String getMessageSelector()
  {
    return messageSelector;
  }

  @Override
  public MessageListener getMessageListener()
  {
    return messageListener;
  }

  @Override
  public void setMessageListener(MessageListener listener)
  {
    messageListener = listener;
    if (messageListener != null)
    {
      session.addWork(this);
    }
  }

  @Override
  public Message receive() throws JMSException
  {
    return receive(Long.MAX_VALUE);
  }

  @Override
  public Message receive(long timeout) throws JMSException
  {
    if (messageListener != null)
    {
      throw new JMSException(Messages.getString("MessageConsumerImpl.0")); //$NON-NLS-1$
    }

    if (timeout == DO_NOT_WAIT)
    {
      return messages.poll();
    }

    try
    {
      return messages.poll(timeout, TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex)
    {
      throw new JMSException(ex.getMessage());
    }
  }

  @Override
  public Message receiveNoWait() throws JMSException
  {
    return receive(DO_NOT_WAIT);
  }

  @Override
  public void close()
  {
    throw new NotYetImplementedException();
  }

  public void handleServerMessage(MessageImpl message)
  {
    messages.add(message);
    if (messageListener != null)
    {
      session.addWork(this);
    }
  }

  public void dispatchMessage()
  {
    MessageListener listener = messageListener;
    if (listener != null)
    {
      MessageImpl message = messages.poll();
      if (message == null)
      {
        OM.LOG.warn(Messages.getString("MessageConsumerImpl.1")); //$NON-NLS-1$
        return;
      }

      try
      {
        listener.onMessage(message);
        if (!session.getTransacted() && session.getAcknowledgeMode() != Session.CLIENT_ACKNOWLEDGE)
        {
          session.acknowledgeMessages(this);
        }
      }
      catch (RuntimeException ex)
      {
        OM.LOG.warn(ex);
      }
    }
  }
}
