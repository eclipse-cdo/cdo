/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms;

import javax.jms.Queue;

public class QueueImpl extends DestinationImpl implements Queue
{
  private static final long serialVersionUID = 1L;

  private String queueName;

  public QueueImpl(String queueName)
  {
    this.queueName = queueName;
  }

  public String getQueueName()
  {
    return queueName;
  }

  public String getName()
  {
    return queueName;
  }
}
