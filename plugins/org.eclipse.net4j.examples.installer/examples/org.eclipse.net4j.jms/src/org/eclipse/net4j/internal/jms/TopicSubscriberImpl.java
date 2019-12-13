/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.TopicSubscriber;

public class TopicSubscriberImpl extends MessageConsumerImpl implements TopicSubscriber
{
  private String name;

  private boolean noLocal;

  public TopicSubscriberImpl(SessionImpl session, long id, TopicImpl topic, String name, String messageSelector, boolean noLocal)
  {
    super(session, id, topic, messageSelector);
    this.name = name;
    this.noLocal = noLocal;
  }

  @Override
  public boolean getNoLocal()
  {
    return noLocal;
  }

  @Override
  public TopicImpl getTopic()
  {
    return (TopicImpl)getDestination();
  }

  public String getName()
  {
    return name;
  }
}
