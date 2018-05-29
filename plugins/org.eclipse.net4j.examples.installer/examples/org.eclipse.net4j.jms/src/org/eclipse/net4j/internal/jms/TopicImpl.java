/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.Topic;

public class TopicImpl extends DestinationImpl implements Topic
{
  private static final long serialVersionUID = 1L;

  private String topicName;

  public TopicImpl(String topicName)
  {
    this.topicName = topicName;
  }

  public String getTopicName()
  {
    return topicName;
  }

  public String getName()
  {
    return topicName;
  }
}
