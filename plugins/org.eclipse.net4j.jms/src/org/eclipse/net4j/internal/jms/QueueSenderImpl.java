/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueSender;

public class QueueSenderImpl extends MessageProducerImpl implements QueueSender
{
  public QueueSenderImpl(SessionImpl session, QueueImpl queue)
  {
    super(session, queue);
  }

  public QueueImpl getQueue()
  {
    return (QueueImpl)getDestination();
  }

  public void send(Queue queue, Message message) throws JMSException
  {
    send(queue, message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    super.send(queue, message, deliveryMode, priority, timeToLive);
  }
}
