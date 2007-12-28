/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jms;

import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;

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

  private long timeToLive = 0;

  private boolean disableMessageTimestamp = false;

  private boolean disableMessageID = false;

  public MessageProducerImpl(SessionImpl session, DestinationImpl destination)
  {
    this.session = session;
    this.destination = destination;
  }

  public SessionImpl getSession()
  {
    return session;
  }

  public int getDeliveryMode()
  {
    return deliveryMode;
  }

  public void setDeliveryMode(int deliveryMode)
  {
    this.deliveryMode = deliveryMode;
  }

  public int getPriority()
  {
    return priority;
  }

  public void setPriority(int priority)
  {
    this.priority = priority;
  }

  public long getTimeToLive()
  {
    return timeToLive;
  }

  public void setTimeToLive(long timeToLive)
  {
    this.timeToLive = timeToLive;
  }

  public DestinationImpl getDestination()
  {
    return destination;
  }

  public boolean getDisableMessageID()
  {
    return disableMessageID;
  }

  public void setDisableMessageID(boolean disableMessageID)
  {
    this.disableMessageID = disableMessageID;
  }

  public boolean getDisableMessageTimestamp()
  {
    return disableMessageTimestamp;
  }

  public void setDisableMessageTimestamp(boolean disableMessageTimestamp)
  {
    this.disableMessageTimestamp = disableMessageTimestamp;
  }

  public void send(Message message) throws JMSException
  {
    send(getDestination(), message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(getDestination(), message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  public void send(Destination destination, Message message) throws JMSException
  {
    send(destination, message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  public void send(Destination destination, Message message, int deliveryMode, int priority, long timeToLive)
      throws JMSException
  {
    if (message == null)
    {
      throw new MessageFormatException("Null message");
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

  public void close()
  {
    throw new NotYetImplementedException();
  }
}
