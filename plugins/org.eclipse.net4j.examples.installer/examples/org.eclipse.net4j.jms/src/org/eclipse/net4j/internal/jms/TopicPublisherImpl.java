/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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
import javax.jms.Topic;
import javax.jms.TopicPublisher;

public class TopicPublisherImpl extends MessageProducerImpl implements TopicPublisher
{
  public TopicPublisherImpl(SessionImpl session, TopicImpl topic)
  {
    super(session, topic);
  }

  @Override
  public TopicImpl getTopic()
  {
    return (TopicImpl)getDestination();
  }

  @Override
  public void publish(Message message) throws JMSException
  {
    publish(getTopic(), message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  @Override
  public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    publish(getTopic(), message, deliveryMode, priority, timeToLive);
  }

  @Override
  public void publish(Topic topic, Message message) throws JMSException
  {
    publish(topic, message, getDeliveryMode(), getPriority(), getTimeToLive());
  }

  @Override
  public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException
  {
    send(topic, message, deliveryMode, priority, timeToLive);
  }
}
