/*
 * Copyright (c) 2007-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.internal.jms.messages.Messages;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.MessageProducer;

import java.util.Date;

public class MessageProducerImpl extends Lifecycle implements MessageProducer
{
  private SessionImpl session;

  private DestinationImpl destination;

  private int deliveryMode = DeliveryMode.PERSISTENT;

  private int priority = Message.DEFAULT_PRIORITY;

  private long timeToLive;

  private boolean disableMessageTimestamp;

  private boolean disableMessageID;

  public MessageProducerImpl(SessionImpl session, DestinationImpl destination)
  {
    this.session = session;
    this.destination = destination;
  }

  public SessionImpl getSession()
  {
    return session;
  }

  @Override
  public int getDeliveryMode()
  {
    return deliveryMode;
  }

  @Override
  public void setDeliveryMode(int deliveryMode)
  {
    this.deliveryMode = deliveryMode;
  }

  @Override
  public int getPriority()
  {
    return priority;
  }

  @Override
  public void setPriority(int priority)
  {
    this.priority = priority;
  }

  @Override
  public long getTimeToLive()
  {
    return timeToLive;
  }

  @Override
  public void setTimeToLive(long timeToLive)
  {
    this.timeToLive = timeToLive;
  }

  @Override
  public DestinationImpl getDestination()
  {
    return destination;
  }

  @Override
  public boolean getDisableMessageID()
  {
    return disableMessageID;
  }

  @Override
  public void setDisableMessageID(boolean disableMessageID)
  {
    this.disableMessageID = disableMessageID;
  }

  @Override
  public boolean getDisableMessageTimestamp()
  {
    return disableMessageTimestamp;
  }

  @Override
  public void setDisableMessageTimestamp(boolean disableMessageTimestamp)
  {
    this.disableMessageTimestamp = disableMessageTimestamp;
  }

  @Override
  public void send(Message message) throws JMSException
  {
    send(getDestination(), message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  @Override
  public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(getDestination(), message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  @Override
  public void send(Destination destination, Message message) throws JMSException
  {
    send(destination, message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  @Override
  public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    if (message == null)
    {
      throw new MessageFormatException(Messages.getString("MessageProducerImpl_0")); //$NON-NLS-1$
    }

    // message.setJMSMessageID(MessageId.create());
    message.setJMSDestination(destination);
    message.setJMSTimestamp(new Date().getTime());
    message.setJMSPriority(priority);

    if (timeToLive > 0)
    {
      message.setJMSExpiration(System.currentTimeMillis() + timeToLive);
    }
    else
    {
      message.setJMSExpiration(0);
    }

    if (destination instanceof JMSTemporaryDestination)
    {
      message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
    }
    else
    {
      message.setJMSDeliveryMode(deliveryMode);
    }

    session.sendMessage(message);
  }

  @Override
  public void close()
  {
    throw new NotYetImplementedException();
  }
}
