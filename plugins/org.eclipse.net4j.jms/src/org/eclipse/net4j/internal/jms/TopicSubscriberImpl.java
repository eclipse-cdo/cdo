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

import javax.jms.TopicSubscriber;

public class TopicSubscriberImpl extends MessageConsumerImpl implements TopicSubscriber
{
  private String name;

  private boolean noLocal;

  public TopicSubscriberImpl(SessionImpl session, long id, TopicImpl topic, String name, String messageSelector,
      boolean noLocal)
  {
    super(session, id, topic, messageSelector);
    this.name = name;
    this.noLocal = noLocal;
  }

  public boolean getNoLocal()
  {
    return noLocal;
  }

  public TopicImpl getTopic()
  {
    return (TopicImpl)getDestination();
  }

  public String getName()
  {
    return name;
  }
}
