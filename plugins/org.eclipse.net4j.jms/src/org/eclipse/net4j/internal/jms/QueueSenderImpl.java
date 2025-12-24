/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

  @Override
  public QueueImpl getQueue()
  {
    return (QueueImpl)getDestination();
  }

  @Override
  public void send(Queue queue, Message message) throws JMSException
  {
    send(queue, message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  @Override
  public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    super.send(queue, message, deliveryMode, priority, timeToLive);
  }
}
