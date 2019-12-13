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

import javax.jms.Queue;

public class QueueImpl extends DestinationImpl implements Queue
{
  private static final long serialVersionUID = 1L;

  private String queueName;

  public QueueImpl(String queueName)
  {
    this.queueName = queueName;
  }

  @Override
  public String getQueueName()
  {
    return queueName;
  }

  @Override
  public String getName()
  {
    return queueName;
  }
}
